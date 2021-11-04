package com.bosonit.springboot.sa2;

import java.io.IOException;
import java.util.stream.Collectors;

import com.bosonit.springboot.sa2.storage.StorageFileNotFoundException;
import com.bosonit.springboot.sa2.storage.StorageInvalidExtensionException;
import com.bosonit.springboot.sa2.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
public class FileUploadController {

	private final StorageService storageService;

	@Autowired
	public FileUploadController(StorageService storageService) {
		this.storageService = storageService;
	}

	@GetMapping("/")
	public ResponseEntity<?> listUploadedFiles(Model model) {

		model.addAttribute("files", storageService.loadAll().map(
				path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
						"serveFile", path.getFileName().toString()).build().toUri().toString())
				.collect(Collectors.toList()));

		return ResponseEntity.ok().build();
	}

	@GetMapping("/files/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

		Resource file = storageService.loadAsResource(filename);
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
				"attachment; filename=\"" + file.getFilename() + "\"").body(file);
	}

	@GetMapping("/setpath")
	public ResponseEntity<?> setPath(@RequestParam("path") String path){
		storageService.setPath(path);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/upload/{tipo}")
	public ResponseEntity<?> handleFileUpload(@RequestParam("file") MultipartFile file, @PathVariable String tipo,
			RedirectAttributes redirectAttributes) {

		storageService.store(file, tipo);
		redirectAttributes.addFlashAttribute("message",
				"You successfully uploaded " + file.getOriginalFilename() + "!");

		return ResponseEntity.ok().build();
	}

	@ExceptionHandler(StorageFileNotFoundException.class)
	public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
		return ResponseEntity.notFound().build();
	}

	@ExceptionHandler(StorageInvalidExtensionException.class)
	public ResponseEntity<?> handleInvalidExtensionException(StorageInvalidExtensionException exc) {
		return new ResponseEntity<>(exc.getMessage(), HttpStatus.BAD_REQUEST);
	}

}
