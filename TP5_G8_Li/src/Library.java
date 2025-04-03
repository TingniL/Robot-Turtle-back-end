import java.util.*;

public class Library {
    private TreeSet<Artist> bibliotheque;
    public static Scanner scanner = new Scanner(System.in);

    Library() {


    }
        public void run () {
            bibliotheque = new TreeSet<Artist>();
            while (true){
                displayMenu();
            }

        }

        public void displayMenu () {
            System.out.print("\n" +
                    "Bienvenue dans votre bibliothèque musicale !\n" +
                    "Tapez 1 pour ajouter un artiste à votre collection.\n" +
                    "Tapez 2 pour pour supprimer un artiste de votre collection. \n" +
                    "Tapez 3 pour lister tous les artistes présents dans votre collection.\n" +
                    "Tapez 4 pour pour ajouter un album à un artiste.\n" +
                    "Tapez 5 pour pour retirer un album à un artiste.\n" +
                    "Tapez 6 pour lister tous les albums d’un artiste donné.\n");
            int a = scanner.nextInt();

                switch (a) {
                    case 1:
                        addArtist();
                        break;
                    case 2:
                        removeArtist();
                        break;
                    case 3:
                        listArtiste();
                        break;
                    case 4:
                        addAlbum();
                        break;
                    case 5:
                        removeAlbum();
                        break;
                    case 6:
                        lisAlbumforArtist();
                        break;
                }
            }

        public void addArtist () {
            Scanner scanner1 = new Scanner(System.in);
            System.out.println("Veuillez entrer l'artiste que vous voules l'ajouter");
            String artiste = scanner1.nextLine();
            Artist arTist = getArtistByName(artiste);
            if (arTist==null) {
                System.out.println("Est-il encore actif ? Tapez oui\n" + "ou non");
                String artiseAct = scanner1.nextLine();
                boolean ArtiseActif = false;
                if (artiseAct.equals("oui")) {
                    ArtiseActif = true;
                } else if (artiseAct.equals("non")) {
                    ArtiseActif = false;
                } else {
                    System.out.println("Invalid saisie");
                }
                List<Album> albums = new ArrayList<>();
                Artist artist = new Artist(artiste, ArtiseActif, albums);
                bibliotheque.add(artist);

            } else {
                System.out.println("Erreur!\n" +
                        "Cettte artiste est deja existe!");
            }
        }

        public void removeArtist () {
            Scanner scanner2= new Scanner(System.in);
            System.out.println("Veuillez saisir le nom de l'artiste");
            String artist = scanner2.nextLine();
            Artist arTist = getArtistByName(artist);
            if (arTist == null) {
                System.out.println("Erreur!\n" +
                        "Cettte artiste n'est pas existe dans la bibliotheque!");

            } else {
                bibliotheque.remove(arTist);
                System.out.println("Cette artiste est retiree");
            }
        }
        private Artist getArtistByName (String artistName){
            for (Iterator i = bibliotheque.iterator(); i.hasNext(); ) {
                Artist artist = (Artist) i.next();
                if (artist.getName().equals(artistName)) {
                    return artist;
                } else return null;
            }
            return null;
        }


        public void listArtiste () {
            for (Iterator i = bibliotheque.iterator(); i.hasNext(); ) {
                Artist artist = (Artist) i.next();
                System.out.println(artist.toString());
            }
        }
        public void addAlbum () {
            Scanner scanner3 = new Scanner(System.in);
            System.out.println("Veuillez entrer le nom de l'artiste");
            String artiste = scanner3.nextLine();
            Artist arTist = getArtistByName(artiste);
            if (arTist != null) {
                System.out.println("Veuillez entrer le nom de l'album");
                String album = scanner3.nextLine();
                if (Artist.getAlbumbyname(album) == null) {
                    System.out.println("En quelle annee cet album est-il sorti?");
                    int annedesort = scanner3.nextInt();
                    List<Song> chansons = new ArrayList<>();

                    System.out.println("Combien de chansons cet album a-t-il?");
                    int nombredesong = scanner3.nextInt();
                    for (int i = 0; i < nombredesong; i++) {
                        chansons.add(createsong(i));
                    }
                    Album aLbum = new Album(album, annedesort, chansons);
                    Artist.addAlbum(aLbum);
                } else {
                    System.out.println("Erreur!\n" +
                            "Cet album est deja existe!");
                }
            }else {
                System.out.println("cet artiste n'existe pas!");
            }

        }

        public Song createsong ( int i){
            Scanner scanner4 = new Scanner(System.in);
            System.out.println("Veuillez entrer le nom de la " + (i+1) + "eme chanson");
            String titre = scanner4.nextLine();
            System.out.println("Veuillez entrer la duree de la " + (i+1) + "eme chanson");
            int duree = scanner4.nextInt();
            Song chan = new Song(titre, duree);
            return chan;
        }

        public void removeAlbum () {
            Scanner scanner5 = new Scanner(System.in);
            System.out.println("Veuillez saisir le nom de l'artiste");
            String artist = scanner5.nextLine();
            Artist arTist = getArtistByName(artist);
            if (arTist == null) {
                System.out.println("Erreur!\n" +
                        "Cettte artiste n'est pas existe dans la bibliotheque!");
            } else {
                System.out.println("Veuillez entrer le nom de l'album");
                String album = scanner5.nextLine();
                if (Artist.getAlbumbyname(album) == null) {
                    System.out.println("Erreur!\n" +
                            "Cet album n'est pas existe!");
                    ;
                } else {
                    Artist.removeAlbum(album);
                    System.out.println("Cet album est retire");
                }
            }
        }

        public void lisAlbumforArtist () {
            Scanner scanner6 = new Scanner(System.in);
            System.out.println("Veuillez saisir le nom de l'artiste");
            String artist = scanner6.nextLine();
            Artist arTist = getArtistByName(artist);
            if (arTist == null) {
                System.out.println("Erreur!\n" +
                        "Cettte artiste n'est pas existe dans la bibliotheque!");
            } else {
                List albums = arTist.getAlbums();
                Collections.sort(albums, String.CASE_INSENSITIVE_ORDER);
                for (Iterator i = albums.iterator(); i.hasNext(); ) {
                    Album album = (Album) i.next();
                    System.out.println(album.toString());
                }
            }

        }


    }


