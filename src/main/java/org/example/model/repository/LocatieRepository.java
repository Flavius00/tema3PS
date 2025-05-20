package org.example.model.repository;

import org.example.model.Locatie;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LocatieRepository {

    public LocatieRepository() {}

    public List<Locatie> getAllLocatii() throws SQLException {
        Connection connection = Repository.getConnection();

        List<Locatie> locatieList = new ArrayList<>();
        String query = "SELECT * FROM locatie";

        try(Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query)){

            while(rs.next()){
                Locatie locatie = new Locatie();
                locatie.setId(rs.getInt("id"));
                locatie.setTara(rs.getString("tara"));
                locatie.setOras(rs.getString("oras"));
                locatie.setStrada(rs.getString("strada"));
                locatie.setNumar(rs.getString("numar"));
                locatieList.add(locatie);
            }
        }
        catch(SQLException e){
            System.out.println("Eroare la preluarea locațiilor din baza de date!" + e.getMessage());
        }
        finally{
            Repository.closeConnection();
        }
        return locatieList;
    }

    public Locatie getLocatieById(int id) throws SQLException {
        Connection connection = Repository.getConnection();

        String query = "SELECT * FROM locatie WHERE id = ?";

        try(PreparedStatement pstmt = connection.prepareStatement(query)){
            pstmt.setInt(1, id);

            try(ResultSet rs = pstmt.executeQuery()){
                if(rs.next()){
                    Locatie locatie = new Locatie();
                    locatie.setId(rs.getInt("id"));
                    locatie.setTara(rs.getString("tara"));
                    locatie.setOras(rs.getString("oras"));
                    locatie.setStrada(rs.getString("strada"));
                    locatie.setNumar(rs.getString("numar"));
                    return locatie;
                }
            }
        }
        catch(SQLException e){
            System.out.println("Eroare la preluarea locației din baza de date!" + e.getMessage());
        }
        finally{
            Repository.closeConnection();
        }
        return null;
    }

    public boolean save(Locatie locatie){
        Connection connection = Repository.getConnection();

        String sql = "INSERT INTO locatie (tara, oras, strada, numar) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, locatie.getTara());
            pstmt.setString(2, locatie.getOras());
            pstmt.setString(3, locatie.getStrada());
            pstmt.setString(4, locatie.getNumar());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    locatie.setId(generatedKeys.getInt(1));
                } else {
                    return false;
                }
            }

            return true;
        } catch (SQLException e) {
            System.err.println("Eroare la salvarea locației: " + e.getMessage());
            return false;
        }
    }
}