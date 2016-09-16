// Configuration de l'API GeoNetwork
// pour embarquer une application du catalogue
// dans un site tier.
var gnCfg = {
  mods: {
    // Application de recherche
    search: {
      hitsPerPage: [10, 50, 100],
      sortBy: ['relevancy', 'title', 'changeDate'],
      basket: [
        'notifications',
        'services',
        'download',
        'walonmap'
      ],
      minimap: {

      },
      filters: [
        {id: 'any', label: {fre: 'Recherche'}},
        {id: 'theme_wallon', label: {fre: 'Thèmes'}},
        {id: 'orgName', label: {fre: 'Propriétaires'}}
        // Ressources officielles ...
        ],
      // Filtre optionnel permettant de surcharger toutes
      // les recherches
      // filter: '',
      // Par exemple, filter: 'resourceType=maps', pour ne rechercher que des cartes
      tabs: 'resourceType',
      facets: ['inspireTheme', 'category']
    },
    // Application panier
    basket: {
      // http://geoportail.wallonie.be/mes-notifications-et-services
      // Les notifications enregistrent pour un utilisateur une liste d'UUIDs
      // Affiche une liste de fiche avec un filtrage rapide sur une facette
      // Notifie les utilisateurs lors de mise à jour des fiches
      notifications: {
        facets: ['resourceType']
      },
      // http://geoportail.wallonie.be/mes-notifications-et-services
      // Les préférées enregistrent pour un utilisateur une liste d'UUIDs
      // Cette liste s'affiche et permet de retrouver une fiche rapidement
      // Dans walonmap, elles ne portent que sur des services
      preferred: {
        filter: 'resourceType:service'
      },
      // http://geoportail.wallonie.be/sites/geoportail/geodata-donwload.html
      // L'extracteur prépare le téléchargement en demandant à l'utilisateur
      // de préciser ses besoins. Ces informations sont ensuite transmisent à
      // un outil d'extraction.
      download: {
        config: {
          formats: 'http://extractor.api.be/formats',
          // or an [{id: 'ESRI Shapefile', label: {fre: 'Fichier de formes'}]
          crs: 'http://extractor.api.be/crs',
          // or an [{id: 'EPSG:4326', label: {fre: 'WGS 84'}]
        },
        aoi: {
          type: ['communes', 'bbox']
        },
        license: {
          // Add form configuration
        },
        userDetails: {
          // Add user form info
        }
      }
    },
    // Application cartographique
    // Non utilisé dans Metawal car walonmap
    map: {
      synchWithMinimap: true|false
    }
  },
  // Dans le cas ou l'on utilise pas
  // le module cartographique par défaut, définir ici
  // comment dialoguer avec un module cartographique autre
  mapLinks: {
    wms: 'walonmap?uuid={{uuid}}&layerurl={{url}}&layer={{name}}'
  }
};

<!-- Container pour l'appli Sextant -->
<div ng-app="gn_search_sextant"
ng-controller="GnCatController"
ng-include="'../../catalog/views/sextant/templates/index.html'"></div>

  <!-- Configuration de l'appli Sextant -->
<script type="text/javascript">
var sxtSettings = {
  modules : {
    search: true,
    panier: true,
    map: false
  },
  facets: ['inspireTheme', 'category']
};
</script>

<!-- Chargement des librairies -->
<script src="http://localhost:8080/geonetwork/static/lib.js"></script>
  <!-- Chargement de l'appli Sextant -->
<script src="http://localhost:8080/geonetwork/static/gn_search_sextant.js"></script>
