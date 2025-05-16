//package com.backend.karyanestApplication.Repositry;
//
//import com.backend.karyanestApplication.Model.Route;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.Optional;
//
//@Repository
//public interface RouteRepository extends JpaRepository<Route, Long> {
//    Route findByPath(@Param("path") String path);
//    Route findByPathEquals(String path);
//    // Repository method for finding all pattern routes
//    @Query("SELECT r FROM Route r WHERE r.path LIKE '%/{id}%'")
//    List<Route> findAllIdPatternRoutes();
//    List<Route> findAllByPath(String path);
//}
