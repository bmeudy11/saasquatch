//package edu.citadel.main.actuator;
//import org.springframework.boot.actuate.health.HealthEndpoint;
//import org.springframework.boot.actuate.metrics.MetricsEndpoint;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//import java.util.HashMap;
//import java.util.Map;
//import org.springframework.beans.factory.annotation.Autowired;
//import io.micrometer.core.instrument.MeterRegistry;
//import io.micrometer.core.instrument.Meter;
//import io.micrometer.core.instrument.Measurement;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.stream.Stream;
//
//// // this one below does not have the actual metric values, the next @restcontroller thing does
////@RestController
////public class CombinedActuatorController {
////    private final HealthEndpoint healthEndpoint;
////    private final MetricsEndpoint metricsEndpoint;
////
////    public CombinedActuatorController(HealthEndpoint healthEndpoint, MetricsEndpoint metricsEndpoint) {
////        this.healthEndpoint = healthEndpoint;
////        this.metricsEndpoint = metricsEndpoint;
////    }
////
////    @GetMapping(value = "/actuator/combined", produces = "application/json")
////    public Map<String, Object> combined() {
////        Map<String, Object> result = new HashMap<>();
////        result.put("health", healthEndpoint.health());
////        result.put("metrics", metricsEndpoint.listNames());
////        return result;
////    }
////
////}
// includes metric values
//@RestController
//public class CombinedActuatorController {
//
//    private final HealthEndpoint healthEndpoint;
//    private final MeterRegistry meterRegistry;
//
//    public CombinedActuatorController(HealthEndpoint healthEndpoint, MeterRegistry meterRegistry) {
//        this.healthEndpoint = healthEndpoint;
//        this.meterRegistry = meterRegistry;
//    }
//
//    @GetMapping(value = "/actuator/combined", produces = "application/json")
//    public Map<String, Object> combined() {
//        Map<String, Object> response = new HashMap<>();
//
//        //health info
//        response.put("health", healthEndpoint.health());
//
//        //metric info with values
//        Map<String, Object> metricsWithValues = new HashMap<>();
//        for (Meter meter : meterRegistry.getMeters()) {
//            double value = 0.0;
//            for (Measurement m : meter.measure()) {
//                value += m.getValue(); //sum measurements per metric
//            }
//            metricsWithValues.put(meter.getId().getName(), value);
//        }
//        response.put("metrics", metricsWithValues);
//
//        return response;
//    }
//}
