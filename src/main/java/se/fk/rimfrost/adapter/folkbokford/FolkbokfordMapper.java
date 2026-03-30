package se.fk.rimfrost.adapter.folkbokford;

import jakarta.enterprise.context.ApplicationScoped;
import se.fk.rimfrost.adapter.folkbokford.dto.FolkbokfordResponse;
import se.fk.rimfrost.adapter.folkbokford.dto.FolkbokfordResponse.Kon;
import se.fk.rimfrost.adapter.folkbokford.dto.ImmutableFolkbokfordResponse;
import se.fk.rimfrost.api.folkbokforing.jaxrsspec.controllers.generatedsource.model.FolkbokforingPersnrGet200Response;

@ApplicationScoped
public class FolkbokfordMapper
{

   public FolkbokfordResponse toFolkbokfordResponse(FolkbokforingPersnrGet200Response apiResponse)
   {
      if (apiResponse == null)
      {
         return null;
      }
      return ImmutableFolkbokfordResponse.builder()
            .id(apiResponse.getId())
            .fornamn(apiResponse.getFornamn())
            .efternamn(apiResponse.getEfternamn())
            .utdelningsadress(apiResponse.getAdress().getUtdelningsadress())
            .postnummer(apiResponse.getAdress().getPostnummer())
            .postort(apiResponse.getAdress().getPostort())
            .careOf(apiResponse.getAdress().getCareOf())
            .kon(apiResponse.getKon() == se.fk.rimfrost.api.folkbokforing.jaxrsspec.controllers.generatedsource.model.Kon.K
                  ? Kon.KVINNA
                  : Kon.MAN)
            .build();
   }

}
