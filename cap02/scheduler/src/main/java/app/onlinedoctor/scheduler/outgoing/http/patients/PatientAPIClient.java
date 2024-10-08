package app.onlinedoctor.scheduler.outgoing.http.patients;

import app.onlinedoctor.scheduler.exceptions.PatientNotFoundException;
import app.onlinedoctor.scheduler.exceptions.ProxyRequestFailureException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Component
public class PatientAPIClient {

   @Value("${application.outgoing.patient-service.host}")
   private String patientServiceHost;
   private RestTemplate patientAPI;

   @PostConstruct
   public void setup() {
      patientAPI = new RestTemplateBuilder()
         .rootUri(patientServiceHost)
         .build();
   }

   public PatientDTO loadPatient(UUID patientId) {
      try {
         return patientAPI.getForObject(
            "/api/v1/patients/{}",
            PatientDTO.class,
            patientId);
      } catch (HttpClientErrorException.NotFound e) {
         throw new PatientNotFoundException(patientId, e);
      } catch (Exception e) {
         throw new ProxyRequestFailureException(e, "patient service");
      }
   }
}
