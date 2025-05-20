package org.example.model.repository;

import org.example.model.Camera;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CameraRepository {

    public CameraRepository() {}

    public List<Camera> getAllCamera() throws SQLException {
        Connection connection = Repository.getConnection();

        List<Camera> cameraList = new ArrayList<>();
        String query = "SELECT * FROM camera";

        try(Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query)){

            while(rs.next()){
                Camera camera = new Camera();
                camera.setId(rs.getInt("id"));
                camera.setIdHotel(rs.getInt("id_hotel"));
                camera.setNrCamera(rs.getString("nr_camera"));
                camera.setPretPerNoapte(rs.getFloat("pret_per_noapte"));
                camera.setIdPoze(rs.getInt("id_poze"));
                cameraList.add(camera);
            }
        }
        catch(SQLException e){
            System.out.println("Eroare la preluarea camerelor din baza de date!" + e.getMessage());
        }
        finally{
            Repository.closeConnection();
        }
        return cameraList;
    }

    public boolean save(Camera camera){
        Connection connection = Repository.getConnection();

        String sql = "INSERT INTO camera (id_hotel, nr_camera, pret_per_noapte, id_poze) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, camera.getIdHotel());
            pstmt.setString(2, camera.getNrCamera());
            pstmt.setFloat(3, camera.getPretPerNoapte());
            pstmt.setInt(4, camera.getIdPoze());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    camera.setId(generatedKeys.getInt(1));
                } else {
                    return false;
                }
            }

            return true;
        } catch (SQLException e) {
            System.err.println("Eroare la salvarea camerei: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Camera camera){
        Connection connection = Repository.getConnection();

        String sql = "UPDATE camera SET id_hotel=?, nr_camera=?, pret_per_noapte=?, id_poze=? WHERE id=?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, camera.getIdHotel());
            pstmt.setString(2, camera.getNrCamera());
            pstmt.setFloat(3, camera.getPretPerNoapte());
            pstmt.setInt(4, camera.getIdPoze());
            pstmt.setInt(5, camera.getId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Eroare la actualizarea camerei: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int id){
        Connection connection = Repository.getConnection();

        String sql = "DELETE FROM camera WHERE id=?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Eroare la ștergerea camerei: " + e.getMessage());
            return false;
        }
    }

    public List<Camera> getCamereByHotel(int idHotel) throws SQLException {
        Connection connection = Repository.getConnection();

        List<Camera> cameraList = new ArrayList<>();
        String query = "SELECT * FROM camera WHERE id_hotel = ?";

        try(PreparedStatement pstmt = connection.prepareStatement(query)){
            pstmt.setInt(1, idHotel);

            try(ResultSet rs = pstmt.executeQuery()){
                while(rs.next()){
                    Camera camera = new Camera();
                    camera.setId(rs.getInt("id"));
                    camera.setIdHotel(rs.getInt("id_hotel"));
                    camera.setNrCamera(rs.getString("nr_camera"));
                    camera.setPretPerNoapte(rs.getFloat("pret_per_noapte"));
                    camera.setIdPoze(rs.getInt("id_poze"));
                    cameraList.add(camera);
                }
            }
        }
        catch(SQLException e){
            System.out.println("Eroare la preluarea camerelor din baza de date!" + e.getMessage());
        }
        finally{
            Repository.closeConnection();
        }
        return cameraList;
    }
}