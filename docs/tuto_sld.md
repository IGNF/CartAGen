# Tutorial to use the CartAGen/GeOxygene SLD/SE symbolization system
This tutorial explains how to use the CartAGen symbolization system based on the SLD/SE OGC standard.

> - Date 23/07/2018.
> - Author: [Guillaume Touya][1]
> - Contact {firstname.lastname}@ign.fr.

The SLD/SE OGC standard
-------------

CartAGen/GeOxygene implementation of the standards
-------------


What does a SLD file look like?
-------------

```xml
  <NamedLayer>
    <Name>cycleWay</Name> <!-- this name corresponds to the layer name, i.e. the
    name of the CartAGen population that is displayed in this layer (CartAGenDataSet.CYCLEWAY_POP in this case) -->
      <UserStyle>
        <FeatureTypeStyle>
          <Rule><!-- a feature type style may have different rules to apply different symbols to different features (e.g. minor roads vs. major roads) -->
            <Filter><!-- a filter that defines what features of the layer apply
            to this specific rule -->
              <PropertyIsEqualTo><!-- only the features with the attribute 'highway' = 'cycleway' have the following symbol  -->
              <PropertyName>highway</PropertyName>
               <Literal>cycleway</Literal>
              </PropertyIsEqualTo>
            </Filter>	 	
          <LineSymbolizer><!-- a line symbolizer is used here as the features are lines. Point, polygon, and text symbolizers also exist -->
            <Stroke><!-- defines the characteristics of the "stroke" component of the symbol -->
              <CssParameter name="stroke">#D391BE</CssParameter><!-- stroke color in RGB -->
              <CssParameter name="stroke-width">2.0</CssParameter><!-- stroke width in pixels -->
              <CssParameter name="stroke-linejoin">round</CssParameter><!-- type of stroke line join -->
              <CssParameter name="stroke-linecap">round</CssParameter><!-- type of stroke line cap -->
              <CssParameter name="stroke-dasharray">6 3</CssParameter><!-- characteristics of the dashes in the line (length of dash in pixels and then length of space in pixels) -->
            </Stroke>
          </LineSymbolizer>          	
        </Rule>  
      </FeatureTypeStyle>		  
    </UserStyle>
  </NamedLayer>
```


How to change the symbols of the map?
-------------


[1]: http://recherche.ign.fr/labos/cogit/english/cv.php?prenom=&nom=Touya
[2]: http://www.opengeospatial.org/standards/sld
[3]: http://www.opengeospatial.org/standards/se
