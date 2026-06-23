package se.fk.rimfrost.adapter.folkbokford;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.ServiceUnavailableException;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.glassfish.jersey.apache5.connector.Apache5ConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.proxy.WebResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fk.rimfrost.adapter.folkbokford.dto.FolkbokfordRequest;
import se.fk.rimfrost.adapter.folkbokford.dto.FolkbokfordResponse;
import se.fk.rimfrost.api.folkbokforing.jaxrsspec.controllers.generatedsource.FolkbokforingControllerApi;
import se.fk.rimfrost.api.folkbokforing.jaxrsspec.controllers.generatedsource.model.FolkbokforingPersnrGet200Response;

@SuppressWarnings("unused")
@ApplicationScoped
public class FolkbokfordAdapter
{

   Logger LOGGER = LoggerFactory.getLogger(FolkbokfordAdapter.class);

   @ConfigProperty(name = "folkbokford.api.base-url")
   String folkbokfordBaseUrl;

   @Inject
   FolkbokfordMapper mapper;

   private FolkbokforingControllerApi folkbokfordClient;

   private Client client;

   @PostConstruct
   void init()
   {
      ClientConfig clientConfig = new ClientConfig();
      clientConfig.connectorProvider(new Apache5ConnectorProvider());
      client = ClientBuilder.newClient(clientConfig);
      this.folkbokfordClient = WebResourceFactory.newResource(
            FolkbokforingControllerApi.class,
            client.target(folkbokfordBaseUrl));
   }

   @PreDestroy
   void destroy()
   {
      this.folkbokfordClient = null;

      if (client != null)
      {
         client.close();
         client = null;
      }
   }

   public FolkbokfordResponse getFolkbokfordInfo(FolkbokfordRequest folkbokfordRequest) throws FolkbokfordException
   {
      FolkbokforingPersnrGet200Response apiResponse = null;
      try
      {
         apiResponse = folkbokfordClient.folkbokforingPersnrGet(folkbokfordRequest.personnummer());

         if (apiResponse == null)
         {
            throw new FolkbokfordException(FolkbokfordException.ErrorType.UNEXPECTED_ERROR,
                  "An unexpected error occurred while fetching folkbokföringsinformation. Response is null for social security number "
                        + folkbokfordRequest.personnummer());
         }
      }
      catch (NotFoundException ex)
      {
         var message = "Folkbokföringsinformation not found for social security number " + folkbokfordRequest.personnummer();
         LOGGER.error(message, ex);
         throw new FolkbokfordException(FolkbokfordException.ErrorType.NOT_FOUND, message);
      }
      catch (BadRequestException ex)
      {
         var message = "Bad request while fetching folkbokföringsinformation for social security number "
               + folkbokfordRequest.personnummer();
         LOGGER.error(message, ex);
         throw new FolkbokfordException(FolkbokfordException.ErrorType.BAD_REQUEST, message);
      }
      catch (ServiceUnavailableException ex)
      {
         var message = "Request could not be handled by server";
         LOGGER.error(message, ex);
         throw new FolkbokfordException(FolkbokfordException.ErrorType.SERVICE_UNAVAILABLE, message);
      }
      catch (ProcessingException | WebApplicationException ex)
      {
         var message = "An unexpected error occurred while fetching folkbokföringsinformation for social security number "
               + folkbokfordRequest.personnummer();
         LOGGER.error(message, ex);
         throw new FolkbokfordException(FolkbokfordException.ErrorType.UNEXPECTED_ERROR, message, ex);
      }

      try
      {
         return mapper.toFolkbokfordResponse(apiResponse);
      }
      catch (Exception ex)
      {
         var message = "An unexpected error occurred while mapping folkbokföringsinformation response for social security number "
               + folkbokfordRequest.personnummer() + " to internal response type";
         LOGGER.error(message, ex);
         throw new FolkbokfordException(FolkbokfordException.ErrorType.UNEXPECTED_ERROR, message, ex);
      }
   }
}
