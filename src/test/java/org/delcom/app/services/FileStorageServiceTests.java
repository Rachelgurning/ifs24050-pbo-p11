package org.delcom.app.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FileStorageServiceTests {

    private FileStorageService fileStorageService;
    private MultipartFile mockMultipartFile;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setup() {
        fileStorageService = new FileStorageService();
        fileStorageService.uploadDir = tempDir.toString();
        mockMultipartFile = mock(MultipartFile.class);
    }

    // --- 1. Tes Normal & Pembuatan Folder (Menghijaukan baris 21-23) ---
    @Test
    void storeFile_buat_directory_baru() throws IOException {
        // Arahkan ke subfolder yang BELUM ADA supaya 'Files.createDirectories' dijalankan
        Path nonExistentDir = tempDir.resolve("folder-baru");
        fileStorageService.uploadDir = nonExistentDir.toString();

        UUID todoId = UUID.randomUUID();
        when(mockMultipartFile.getOriginalFilename()).thenReturn("test.jpg");
        when(mockMultipartFile.getInputStream()).thenReturn(new ByteArrayInputStream("data".getBytes()));

        String result = fileStorageService.storeFile(mockMultipartFile, todoId);

        // Pastikan folder berhasil dibuat
        assertTrue(Files.exists(nonExistentDir)); 
        assertTrue(Files.exists(nonExistentDir.resolve(result)));
    }

    // --- 2. Tes Filename NULL (Menghijaukan baris 29) ---
    @Test
    void storeFile_filename_null() throws IOException {
        UUID todoId = UUID.randomUUID();
        // Simulasi nama file null
        when(mockMultipartFile.getOriginalFilename()).thenReturn(null);
        when(mockMultipartFile.getInputStream()).thenReturn(new ByteArrayInputStream("data".getBytes()));

        String result = fileStorageService.storeFile(mockMultipartFile, todoId);

        // Harusnya tidak error, tapi ekstensi kosong
        assertTrue(result.startsWith("cover_"));
        assertFalse(result.endsWith(".")); 
    }

    // --- 3. Tes Filename Tanpa Titik (Menghijaukan logika 'contains(".")') ---
    @Test
    void storeFile_filename_tanpa_ekstensi() throws IOException {
        UUID todoId = UUID.randomUUID();
        // Simulasi nama file tanpa titik
        when(mockMultipartFile.getOriginalFilename()).thenReturn("filetanpatitik");
        when(mockMultipartFile.getInputStream()).thenReturn(new ByteArrayInputStream("data".getBytes()));

        String result = fileStorageService.storeFile(mockMultipartFile, todoId);

        // Harusnya tersimpan tanpa ekstensi
        assertTrue(result.contains(todoId.toString()));
        assertFalse(result.contains(".")); 
    }

    // --- 4. Tes Delete Normal ---
    @Test
    void deleteFile_berhasil() throws IOException, InterruptedException {
        String filename = "hapus.txt";
        Path filePath = tempDir.resolve(filename);
        Files.write(filePath, "data".getBytes());
        
        // GC untuk windows lock issue
        System.gc();
        Thread.sleep(50);

        boolean result = fileStorageService.deleteFile(filename);
        assertTrue(result);
    }

    // --- 5. Tes Delete Gagal (File Tidak Ada) ---
    @Test
    void deleteFile_file_tidak_ada() {
        boolean result = fileStorageService.deleteFile("ghost.txt");
        assertFalse(result);
    }

    // --- 6. Tes Delete Exception (Menghijaukan catch IOException) ---
    @Test
    void deleteFile_io_exception_lock() throws IOException {
        String filename = "locked.txt";
        Path filePath = tempDir.resolve(filename);
        Files.write(filePath, "data".getBytes());

        // TRIK: Kita buka file stream dan TAHAN (jangan diclose dulu)
        // Di Windows, file yang sedang dibuka TIDAK BISA DIHAPUS.
        // Ini akan memaksa 'Files.deleteIfExists' melempar IOException.
        FileInputStream fis = new FileInputStream(filePath.toFile());
        
        try {
            // Coba hapus saat file masih terbuka stream-nya
            boolean result = fileStorageService.deleteFile(filename);
            
            // Harusnya FALSE karena masuk blok catch(IOException)
            assertFalse(result); 
        } finally {
            // Tutup stream biar bisa dihapus nanti oleh sistem
            fis.close();
        }
    }

    // --- 7. Tes Load File & Exists ---
    @Test
    void load_and_check_exists() throws IOException {
        String filename = "cek.txt";
        Files.createFile(tempDir.resolve(filename));
        
        Path loaded = fileStorageService.loadFile(filename);
        assertEquals(tempDir.resolve(filename).toAbsolutePath(), loaded.toAbsolutePath());
        
        assertTrue(fileStorageService.fileExists(filename));
        assertFalse(fileStorageService.fileExists("raib.txt"));
    }
}