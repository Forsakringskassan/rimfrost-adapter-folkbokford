package se.fk.rimfrost.adapter.folkbokford;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import se.fk.github.jaxrsclientfactory.JaxrsClientFactory;
import se.fk.github.jaxrsclientfactory.JaxrsClientOptionsBuilders;
import se.fk.rimfrost.adapter.folkbokford.dto.FolkbokfordRequest;
import se.fk.rimfrost.adapter.folkbokford.dto.FolkbokfordResponse;
import se.fk.rimfrost.api.folkbokforing.jaxrsspec.controllers.generatedsource.FolkbokforingControllerApi;

@SuppressWarnings("unused")
@ApplicationScoped
public class FolkbokfordAdapter
{

   @ConfigProperty(name = "folkbokford.api.base-url")
   String folkbokfordBaseUrl;

   @Inject
   FolkbokfordMapper mapper;

   private FolkbokforingControllerApi folkbokfordClient;

   @PostConstruct
   void init()
   {
      this.folkbokfordClient = new JaxrsClientFactory()
            .create(JaxrsClientOptionsBuilders.createClient(folkbokfordBaseUrl, FolkbokforingControllerApi.class)
                  .build());
   }

   public FolkbokfordResponse getFolkbokfordInfo(FolkbokfordRequest folkbokfordRequest)
   {
      try
      {
         var apiResponse = folkbokfordClient.folkbokforingPersnrGet(folkbokfordRequest.personnummer());
         return mapper.toFolkbokfordResponse(apiResponse);
      }
      catch (WebApplicationException ex)
      {
         if (ex.getResponse().getStatus() == 404)
         {
            // return null or an empty response instead of throwing
            return null;
         }
         throw ex; // rethrow other exceptions
      }
   }
}
