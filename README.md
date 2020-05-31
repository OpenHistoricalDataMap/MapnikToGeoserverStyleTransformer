# MapnikToGeoserverStyleTransformer

Author: Leon Lüttger   
Email: Leon.Luettger@Student.HTW-Berlin.de

### Wichtige Ressourcen
* [Bachelor Arbeit Ebert](http://www.sharksystem.net/htw/FP_ICW_BA_MA/2019_Ebert_Bachelorarbeit.pdf)
* [Bachelor Arbeit Ebert Folien](http://www.sharksystem.net/htw/FP_ICW_BA_MA/2019_Ebert_Bachelorarbeit_Folien.pdf)
* [Mapnik CSS](https://github.com/gravitystorm/openstreetmap-carto)
* [Mapnik Zoom](https://github.com/openstreetmap/mapnik-stylesheets/blob/master/zoom-to-scale.txt)
* [Geoserver CSS Properties](https://docs.geoserver.org/stable/en/user/styling/css/properties.html#css-properties)

### Wichtige Hinweise
Das Übersetzten von Werten, etc. wird in den Klassen des Packages `translate` definiert.

### Funktionsweise (Wichtige Ablaufschritte)
1. Analysieren aller Nodes um Variablen zu cachen (sind teilweise wild verteilt...)
2. Analysieren aller Nodes um Node Baum aufzubauen
3. Aufteilen von gruppierten Selektoren
4. Aufteilen von verschachtelten Selektoren
5. Aufbauen des finalen Baum (+ Übersetzung der Werte beim einfügen)
6. Schreiben des finalen Baums in eine `.css` Datei
7. Alle benötigen Ressourcen (z.B. Bilder) kopieren

### Bekannte Fehler
* Code Qualität
* Style-Methoden wie z.B. `darken(...)` werden nicht aufgelöst
