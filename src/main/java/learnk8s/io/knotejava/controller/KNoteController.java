package learnk8s.io.knotejava.controller;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import learnk8s.io.knotejava.config.KnoteProperties;
import learnk8s.io.knotejava.entities.NoteEntity;
import learnk8s.io.knotejava.repositories.NotesRepository;

@Controller
public class KNoteController {
    
    @Autowired
    private NotesRepository notesRepository;
    
    private Parser parser = Parser.builder().build();
    
    private HtmlRenderer renderer = HtmlRenderer.builder().build();

    @Autowired
    private KnoteProperties properties;

    @GetMapping("/")
    public String index(Model model) {
        getAllNotes(model);
        return "index";
    }
    
    @PostMapping("/note")
    public String saveNotes(@RequestParam("image") MultipartFile file,
                            @RequestParam String description,
                            @RequestParam(required = false) String publish,
                            @RequestParam(required = false) String upload,
                            Model model) throws Exception {
        if (publish != null && publish.equals("Publish")) {
            saveNote(description, model);
            getAllNotes(model);
            return "redirect:/";
        }
        if (upload != null && upload.equals("Upload")) {
            if (file != null && file.getOriginalFilename() != null
                    && !file.getOriginalFilename().isEmpty()) {
            	uploadImage(file, description, model);
            }
            getAllNotes(model);
            return "index";
        }
        return "index";
        
    }
  
    private void getAllNotes(Model model) {
        List<NoteEntity> notes = notesRepository.findAll();
        Collections.reverse(notes);
        model.addAttribute("notes", notes);
    }
    
    private void saveNote(String description, Model model) {
        if (description != null && !description.trim().isEmpty()) {
            //You need to translate markup to HTML
            Node document = parser.parse(description.trim());
            String html = renderer.render(document);
            notesRepository.save(new NoteEntity(null, html));
            //After publish you need to clean up the textarea
            model.addAttribute("description", "");
        }        
    }
    
    private void uploadImage(MultipartFile file, String description, Model model) throws Exception {
        File uploadsDir = new File(properties.getUploadDir());
        if (!uploadsDir.exists()) {
            uploadsDir.mkdir();
        }
        String fileId = UUID.randomUUID().toString() + "."
                + file.getOriginalFilename().split("\\.")[1];
        File f = new File(uploadsDir, fileId);
        file.transferTo(f.toPath());
        model.addAttribute("description", description + " ![](/uploads/" + fileId + ")");
    }
   
}