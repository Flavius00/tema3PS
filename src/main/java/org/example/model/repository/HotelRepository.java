package org.example.model.repository;

import org.example.model.Hotel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HotelRepository {

    public HotelRepository() {}

    public List<Hotel> getAllHotels() throws SQLException {
        Connection connection = Repository.getConnection();

        List<Hotel> hotelList = new ArrayList<>();
        String query = "SELECT * FROM hotel";

        try(Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query)){

            while(rs.next()){
                Hotel hotel = new Hotel();
                hotel.setId(rs.getInt("id"));
                hotel.setNume(rs.getString("nume"));
                hotel.setIdLocatie(rs.getInt("id_locatie"));
                hotel.setNrTelefon(rs.getString("nr_telefon"));
                hotel.setEmail(rs.getString("email"));
                hotel.setFacilitati(rs.getString("facilitati"));
                hotel.setIdLant(rs.getInt("id_lant"));
                hotelList.add(hotel);
            }
        }
        catch(SQLException e){
            System.out.println("Eroare la preluarea hotelurilor din baza de date!" + e.getMessage());
        }
        finally{
            Repository.closeConnection();
        }
        return hotelList;
    }

    public boolean save(Hotel hotel){
        Connection connection = Repository.getConnection();

        String sql = "INSERT INTO hotel (nume, id_locatie, nr_telefon, email, facilitati, id_lant) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, hotel.getNume());
            pstmt.setInt(2, hotel.getIdLocatie());
            pstmt.setString(3, hotel.getNrTelefon());
            pstmt.setString(4, hotel.getEmail());
            pstmt.setString(5, hotel.getFacilitati());
            pstmt.setInt(6, hotel.getIdLant());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    hotel.setId(generatedKeys.getInt(1));
                } else {
                    return false;
                }
            }

            return true;
        } catch (SQLException e) {
            System.err.println("Eroare la salvarea hotelului: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Hotel hotel){
        Connection connection = Repository.getConnection();

        String sql = "UPDATE hotel SET nume=?, id_locatie=?, nr_telefon=?, email=?, facilitati=?, id_lant=? WHERE id=?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, hotel.getNume());
            pstmt.setInt(2, hotel.getIdLocatie());
            pstmt.setString(3, hotel.getNrTelefon());
            pstmt.setString(4, hotel.getEmail());
            pstmt.setString(5, hotel.getFacilitati());
            pstmt.setInt(6, hotel.getIdLant());
            pstmt.setInt(7, hotel.getId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Eroare la actualizarea hotelului: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int id){
        Connection connection = Repository.getConnection();

        String sql = "DELETE FROM hotel WHERE id=?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Eroare la ștergerea hotelului: " + e.getMessage());
            return false;
        }
    }

    public List<Hotel> getHotelsByLant(int idLant) throws SQLException {
        Connection connection = Repository.getConnection();

        List<Hotel> hotelList = new ArrayList<>();
        String query = "SELECT * FROM hotel WHERE id_lant = ?";

        try(PreparedStatement pstmt = connection.prepareStatement(query)){
            pstmt.setInt(1, idLant);

            try(ResultSet rs = pstmt.executeQuery()){
                while(rs.next()){
                    Hotel hotel = new Hotel();
                    hotel.setId(rs.getInt("id"));
                    hotel.setNume(rs.getString("nume"));
                    hotel.setIdLocatie(rs.getInt("id_locatie"));
                    hotel.setNrTelefon(rs.getString("nr_telefon"));
                    hotel.setEmail(rs.getString("email"));
                    hotel.setFacilitati(rs.getString("facilitati"));
                    hotel.setIdLant(rs.getInt("id_lant"));
                    hotelList.add(hotel);
                }
            }
        }
        catch(SQLException e){
            System.out.println("Eroare la preluarea hotelurilor din baza de date!" + e.getMessage());
        }
        finally{
            Repository.closeConnection();
        }
        return hotelList;
    }
}