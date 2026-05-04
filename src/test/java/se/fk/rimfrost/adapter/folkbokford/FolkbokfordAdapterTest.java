package se.fk.rimfrost.adapter.folkbokford;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.quarkus.test.component.QuarkusComponentTest;
import java.util.UUID;

import jakarta.ws.rs.WebApplicationException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import se.fk.rimfrost.adapter.folkbokford.FolkbokfordMapper;
import se.fk.rimfrost.adapter.folkbokford.dto.FolkbokfordResponse;
import se.fk.rimfrost.adapter.folkbokford.dto.ImmutableFolkbokfordRequest;
import se.fk.rimfrost.adapter.folkbokford.dto.ImmutableFolkbokfordResponse;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

@QuarkusComponentTest(value =
{
      FolkbokfordMapper.class
})
public class FolkbokfordAdapterTest
{
   private static WireMockServer server;

   @BeforeAll
   public static void setup()
   {
      server = new WireMockServer(
            options()
                  .dynamicPort()
                  .usingFilesUnderDirectory("src/test/resources"));
      server.start();

      System.setProperty("folkbokford.api.base-url", server.baseUrl());
   }

   @AfterAll
   public static void teardown()
   {
      if (server != null)
      {
         server.stop();
      }
   }

   @Test
   void testGetFolkbokford200(FolkbokfordAdapter folkbokfordAdapter)
   {
      var expectedResponse = ImmutableFolkbokfordResponse.builder()
            .id("19990101-9999")
            .fornamn("fornamn")
            .efternamn("efternamn")
            .utdelningsadress("utdelningsadress")
            .postnummer("postnummer")
            .postort("postort")
            .careOf("careOf")
            .kon(FolkbokfordResponse.Kon.MAN)
            .build();

      var folkbokfordRequest = ImmutableFolkbokfordRequest.builder()
            .personnummer("19990101-9999")
            .build();

      try
      {
         var response = folkbokfordAdapter.getFolkbokfordInfo(folkbokfordRequest);
         assertEquals(expectedResponse, response);
      }
      catch (FolkbokfordException fkde)
      {
         fkde.printStackTrace();
         fail(fkde.getMessage());
      }
   }

   @Test
   void testGetFolkbokford400(FolkbokfordAdapter folkbokfordAdapter)
   {
      var folkbokfordRequest = ImmutableFolkbokfordRequest.builder()
            .personnummer("19990102-9999")
            .build();

      try
      {
         folkbokfordAdapter.getFolkbokfordInfo(folkbokfordRequest);
         fail();
      }
      catch (FolkbokfordException e)
      {
         assertEquals(FolkbokfordException.ErrorType.BAD_REQUEST, e.getErrorType());
      }
   }

   @Test
   void testGetFolkbokford404(FolkbokfordAdapter folkbokfordAdapter)
   {
      var folkbokfordRequest = ImmutableFolkbokfordRequest.builder()
            .personnummer("19990103-9999")
            .build();

      try
      {
         folkbokfordAdapter.getFolkbokfordInfo(folkbokfordRequest);
         fail();
      }
      catch (FolkbokfordException e)
      {
         assertEquals(FolkbokfordException.ErrorType.NOT_FOUND, e.getErrorType());
      }
   }

   @Test
   void testGetFolkbokford500(FolkbokfordAdapter folkbokfordAdapter)
   {
      var folkbokfordRequest = ImmutableFolkbokfordRequest.builder()
            .personnummer("19990104-9999")
            .build();

      try
      {
         folkbokfordAdapter.getFolkbokfordInfo(folkbokfordRequest);
         fail();
      }
      catch (FolkbokfordException e)
      {
         assertEquals(FolkbokfordException.ErrorType.UNEXPECTED_ERROR, e.getErrorType());
      }
   }

   @Test
   void testGetFolkbokford503(FolkbokfordAdapter folkbokfordAdapter)
   {
      var folkbokfordRequest = ImmutableFolkbokfordRequest.builder()
            .personnummer("19990101-1234")
            .build();

      try
      {
         folkbokfordAdapter.getFolkbokfordInfo(folkbokfordRequest);
         fail();
      }
      catch (FolkbokfordException e)
      {
         assertEquals(FolkbokfordException.ErrorType.SERVICE_UNAVAILABLE, e.getErrorType());
      }
   }

   @Test
   void testGetFolkbokfordMapperError(FolkbokfordAdapter folkbokfordAdapter)
   {
      var folkbokfordRequest = ImmutableFolkbokfordRequest.builder()
            .personnummer("19990101-2222")
            .build();

      try
      {
         folkbokfordAdapter.getFolkbokfordInfo(folkbokfordRequest);
         fail();
      }
      catch (FolkbokfordException e)
      {
         assertEquals(FolkbokfordException.ErrorType.UNEXPECTED_ERROR, e.getErrorType());
      }
   }
}
