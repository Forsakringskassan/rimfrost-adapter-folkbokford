package se.fk.rimfrost.adapter.folkbokford.dto;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@JsonSerialize(as = ImmutableFolkbokfordResponse.class)
@JsonDeserialize(as = ImmutableFolkbokfordResponse.class)
public interface FolkbokfordResponse
{
   String id();

   String fornamn();

   String efternamn();

   Kon kon();

   String utdelningsadress();

   String postnummer();

   String postort();

   String careOf();

   public enum Kon
   {
      MAN, KVINNA
   }
}
