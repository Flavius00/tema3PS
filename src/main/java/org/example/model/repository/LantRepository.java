package org.example.model.repository;

import org.example.model.Lant;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LantRepository {

    public LantRepository() {}

    public List<Lant> getAllLanturi() throws SQLException {
        Connection connection = Repository.getConnection();

        List<Lant> lantList = new ArrayList<>();
        String query = "SELECT * FROM lant";

        try(Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query)){

            while(rs.next()){
                Lant lant = new Lant();
                lant.setId(rs.getInt("id"));
                lant.setNume(rs.getString("nume"));
                lantList.add(lant);
            }
        }
        catch(SQLException e){
            System.out.println("Eroare la preluarea lanțurilor din baza de date!" + e.getMessage());
        }
        finally{
            Repository.closeConnection();
        }
        return lantList;
    }

    public Lant getLantById(int id) throws SQLException {
        Connection connection = Repository.getConnection();

        String query = "SELECT * FROM lant WHERE id = ?";

        try(PreparedStatement pstmt = connection.prepareStatement(query)){
            pstmt.setInt(1, id);

            try(ResultSet rs = pstmt.executeQuery()){
                if(rs.next()){
                    Lant lant = new Lant();
                    lant.setId(rs.getInt("id"));
                    lant.setNume(rs.getString("nume"));
                    return lant;
                }
            }
        }
        catch(SQLException e){
            System.out.println("Eroare la preluarea lanțului din baza de date!" + e.getMessage());
        }
        finally{
            Repository.closeConnection();
        }
        return null;
    }

    public boolean save(Lant lant){
        Connection connection = Repository.getConnection();

        String sql = "INSERT INTO lant (nume) VALUES (?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, lant.getNume());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    lant.setId(generatedKeys.getInt(1));
                } else {
                    return false;
                }
            }

            return true;
        } catch (SQLException e) {
            System.err.println("Eroare la salvarea lanțului: " + e.getMessage());
            return false;
        }
    }
}