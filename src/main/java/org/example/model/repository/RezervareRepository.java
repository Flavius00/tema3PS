package org.example.model.repository;

import org.example.model.Rezervare;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RezervareRepository {

    public RezervareRepository() {}

    public List<Rezervare> getAllRezervari() throws SQLException {
        Connection connection = Repository.getConnection();

        List<Rezervare> rezervariList = new ArrayList<>();
        String query = "SELECT * FROM rezervare";

        try(Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query)){

            while(rs.next()){
                int id = rs.getInt("id");
                LocalDateTime startDate = rs.getTimestamp("start_date").toLocalDateTime();
                LocalDateTime endDate = rs.getTimestamp("end_date").toLocalDateTime();
                int idCamera = rs.getInt("id_camera");
                String numeClient = rs.getString("nume_client");
                String prenumeClient = rs.getString("prenume_client");
                String telefonClient = rs.getString("telefon_client");
                String emailClient = rs.getString("email_client");

                Rezervare rezervare = new Rezervare(id, startDate, endDate, idCamera,
                        numeClient, prenumeClient, telefonClient, emailClient);
                rezervariList.add(rezervare);
            }
        }
        catch(SQLException e){
            System.out.println("Eroare la preluarea rezervărilor din baza de date!" + e.getMessage());
        }
        finally{
            Repository.closeConnection();
        }
        return rezervariList;
    }

    public boolean save(Rezervare rezervare){
        Connection connection = Repository.getConnection();

        String sql = "INSERT INTO rezervare (start_date, end_date, id_camera, nume_client, prenume_client, telefon_client, email_client) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setTimestamp(1, Timestamp.valueOf(rezervare.getStartDate()));
            pstmt.setTimestamp(2, Timestamp.valueOf(rezervare.getEndDate()));
            pstmt.setInt(3, rezervare.getIdCamera());
            pstmt.setString(4, rezervare.getNumeClient());
            pstmt.setString(5, rezervare.getPrenumeClient());
            pstmt.setString(6, rezervare.getTelefonClient());
            pstmt.setString(7, rezervare.getEmailClient());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    rezervare.setId(generatedKeys.getInt(1));
                } else {
                    return false;
                }
            }

            return true;
        } catch (SQLException e) {
            System.err.println("Eroare la salvarea rezervării: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Rezervare rezervare){
        Connection connection = Repository.getConnection();

        String sql = "UPDATE rezervare SET start_date=?, end_date=?, id_camera=?, nume_client=?, prenume_client=?, telefon_client=?, email_client=? WHERE id=?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setTimestamp(1, Timestamp.valueOf(rezervare.getStartDate()));
            pstmt.setTimestamp(2, Timestamp.valueOf(rezervare.getEndDate()));
            pstmt.setInt(3, rezervare.getIdCamera());
            pstmt.setString(4, rezervare.getNumeClient());
            pstmt.setString(5, rezervare.getPrenumeClient());
            pstmt.setString(6, rezervare.getTelefonClient());
            pstmt.setString(7, rezervare.getEmailClient());
            pstmt.setInt(8, rezervare.getId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Eroare la actualizarea rezervării: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int id){
        Connection connection = Repository.getConnection();

        String sql = "DELETE FROM rezervare WHERE id=?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Eroare la ștergerea rezervării: " + e.getMessage());
            return false;
        }
    }

    public List<Rezervare> getRezervariByHotelAndDate(int idHotel, LocalDateTime date) throws SQLException {
        Connection connection = Repository.getConnection();

        List<Rezervare> rezervariList = new ArrayList<>();
        String query = "SELECT r.* FROM rezervare r " +
                "JOIN camera c ON r.id_camera = c.id " +
                "WHERE c.id_hotel = ? AND ? BETWEEN r.start_date AND r.end_date";

        try(PreparedStatement pstmt = connection.prepareStatement(query)){
            pstmt.setInt(1, idHotel);
            pstmt.setTimestamp(2, Timestamp.valueOf(date));

            try(ResultSet rs = pstmt.executeQuery()){
                while(rs.next()){
                    int id = rs.getInt("id");
                    LocalDateTime startDate = rs.getTimestamp("start_date").toLocalDateTime();
                    LocalDateTime endDate = rs.getTimestamp("end_date").toLocalDateTime();
                    int idCamera = rs.getInt("id_camera");
                    String numeClient = rs.getString("nume_client");
                    String prenumeClient = rs.getString("prenume_client");
                    String telefonClient = rs.getString("telefon_client");
                    String emailClient = rs.getString("email_client");

                    Rezervare rezervare = new Rezervare(id, startDate, endDate, idCamera,
                            numeClient, prenumeClient, telefonClient, emailClient);
                    rezervariList.add(rezervare);
                }
            }
        }
        catch(SQLException e){
            System.out.println("Eroare la preluarea rezervărilor din baza de date!" + e.getMessage());
        }
        finally{
            Repository.closeConnection();
        }
        return rezervariList;
    }
}