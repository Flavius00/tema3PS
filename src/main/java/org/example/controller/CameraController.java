package org.example.controller;

import org.example.model.Camera;
import org.example.model.repository.CameraRepository;
import org.example.model.Observable;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class CameraController extends Observable {
    private CameraRepository cameraRepository;
    private RezervareController rezervareController;

    public CameraController(CameraRepository cameraRepository, RezervareController rezervareController) {
        this.cameraRepository = cameraRepository;
        this.rezervareController = rezervareController;
    }

    public List<Camera> getAllCamere() throws SQLException {
        return cameraRepository.getAllCamera();
    }

    public List<Camera> getCamereByHotel(int idHotel) throws SQLException {
        return cameraRepository.getAllCamera().stream()
                .filter(camera -> camera.getIdHotel() == idHotel)
                .collect(Collectors.toList());
    }

    public boolean addCamera(int idHotel, String nrCamera, float pretPerNoapte, int idPoze) {
        Camera camera = new Camera();
        camera.setIdHotel(idHotel);
        camera.setNrCamera(nrCamera);
        camera.setPretPerNoapte(pretPerNoapte);
        camera.setIdPoze(idPoze);

        boolean success = cameraRepository.save(camera);
        if (success) {
            notifyObservers();
        }
        return success;
    }

    public boolean updateCamera(int id, int idHotel, String nrCamera, float pretPerNoapte, int idPoze) {
        Camera camera = new Camera();
        camera.setId(id);
        camera.setIdHotel(idHotel);
        camera.setNrCamera(nrCamera);
        camera.setPretPerNoapte(pretPerNoapte);
        camera.setIdPoze(idPoze);

        boolean success = cameraRepository.update(camera);
        if (success) {
            notifyObservers();
        }
        return success;
    }

    public boolean deleteCamera(int id) {
        boolean success = cameraRepository.delete(id);
        if (success) {
            notifyObservers();
        }
        return success;
    }

    public List<Camera> filterCamereByPret(int idHotel, float pretMin, float pretMax) throws SQLException {
        return getCamereByHotel(idHotel).stream()
                .filter(camera -> camera.getPretPerNoapte() >= pretMin && camera.getPretPerNoapte() <= pretMax)
                .collect(Collectors.toList());
    }

    public List<Camera> getCamereLibereByDate(int idHotel, LocalDateTime date) throws SQLException {
        List<Camera> toateCamerele = getCamereByHotel(idHotel);
        List<Integer> camereRezervate = rezervareController.getCamereRezervateByDate(date);

        return toateCamerele.stream()
                .filter(camera -> !camereRezervate.contains(camera.getId()))
                .collect(Collectors.toList());
    }

    public List<Camera> getCamereRezervateByDate(int idHotel, LocalDateTime date) throws SQLException {
        List<Camera> toateCamerele = getCamereByHotel(idHotel);
        List<Integer> camereRezervate = rezervareController.getCamereRezervateByDate(date);

        return toateCamerele.stream()
                .filter(camera -> camereRezervate.contains(camera.getId()))
                .collect(Collectors.toList());
    }
}