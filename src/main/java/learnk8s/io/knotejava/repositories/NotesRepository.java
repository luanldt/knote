package learnk8s.io.knotejava.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import learnk8s.io.knotejava.entities.NoteEntity;

public interface NotesRepository extends MongoRepository<NoteEntity, String> {

}