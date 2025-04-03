import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Artist implements Comparable{
    private String Name;
    private boolean ArtiseActif;
    private static List<Album> albums;
    public Artist(){

    }
    public Artist(String Name,boolean ArtiseActif,List<Album> albums){
        this.Name=Name;
        this.ArtiseActif = ArtiseActif;
        this.albums = albums;
    }

    @Override
    public int compareTo(Object o) {
        Artist other = (Artist) o;
        return this.getName().compareTo(other.getName());
    }

    public static List getAlbums(){

        return albums;
    }
    public  String getName(){

        return Name;
    }
    public static void addAlbum(Album album){
        albums.add(album);

    }
    public static void removeAlbum(String albumName){
        albums.remove(albumName);

    }
    public static Album getAlbumbyname(String albumName){
        for (Iterator i = albums.iterator(); i.hasNext(); ) {
            Album album = (Album) i.next();
            if (album.getTitre().equals(albumName)) {
                return album;
            } else return null;
        }
        return null;
    }
    public String toString(){
        return getName();
    }


}
