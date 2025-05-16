//package com.backend.karyanestApplication.Service;
//
//import com.backend.karyanestApplication.Exception.CustomException;
//import com.backend.karyanestApplication.Model.Route;
//import com.backend.karyanestApplication.Repositry.RouteRepository;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//@Service
//public class RouteService {
//
//    private final RouteRepository routeRepository;
//
//    public RouteService(RouteRepository routeRepository) {
//        this.routeRepository = routeRepository;
//    }
//
//    // ✅ Create a new route
//    @Transactional
//    public Route createRoute(Route route) {
//        return routeRepository.save(route);
//    }
//
//    // ✅ Retrieve all routes
//    @Transactional(readOnly = true)
//    public List<Route> getAllRoutes() {
//        return routeRepository.findAll();
//    }
//
//    // ✅ Retrieve route by ID
//    @Transactional(readOnly = true)
//    public Route getRouteById(Long id) {
//        return routeRepository.findById(id)
//                .orElseThrow(() -> new CustomException("Route not found for this ID: " + id));
//    }
//
//    // ✅ Retrieve route by path
//    @Transactional(readOnly = true)
//    public Route getRouteByPath(String path) {
//        return routeRepository.findByPath(path);
//    }
//
//    // ✅ Update route
//    @Transactional
//    public Route updateRoute(Long id, Route routeDetails) {
//        Route route = getRouteById(id); // Ensuring the route exists
//        route.setName(routeDetails.getName());
//        route.setPath(routeDetails.getPath());
//        return routeRepository.save(route);
//    }
//
//    // ✅ Delete route
//    @Transactional
//    public void deleteRoute(Long id) {
//        Route route = getRouteById(id); // Ensuring the route exists
//        routeRepository.delete(route);
//    }
//}
