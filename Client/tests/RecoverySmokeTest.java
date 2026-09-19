
import org.newdawn.slick.*;
import com.client.gamestates.*;
import com.client.entities.*;
import com.client.map.Tile;
import com.client.network.NetworkManager;
import org.newdawn.slick.geom.Vector2f;
import de.matthiasmann.twl.EditField;
import java.lang.reflect.*;
import java.util.ArrayList;
public class RecoverySmokeTest extends Base {
 private int last=-1, frames=0;
 protected void postRenderState(GameContainer c, Graphics g) throws SlickException {
  int state=getCurrentStateID();
  if(state!=last){last=state;frames=0;System.out.println("STATE="+getCurrentState().getClass().getName());}
  ++frames;
  try {
   if(state==IDENTIFICATION && frames==30){
    Object login=getCurrentState();
    Field u=login.getClass().getDeclaredField("ef_login");u.setAccessible(true);((EditField)u.get(login)).setText(System.getProperty("test.user"));
    Field p=login.getClass().getDeclaredField("ef_password");p.setAccessible(true);((EditField)p.get(login)).setText(System.getProperty("test.password"));
    Method m=login.getClass().getDeclaredMethod("test");m.setAccessible(true);m.invoke(login);
   }
   if(state==CHOIX_PERSO && frames==30){
    Field f=getCurrentState().getClass().getDeclaredField("persos");f.setAccessible(true);
    Joueur player=(Joueur)((ArrayList)f.get(getCurrentState())).get(0);
    new MainJoueur(player.getPerso(),null,player.getOrientation());
    MainJoueur.instance.setTile(new Tile(new Vector2f(24,43),null));
    NetworkManager.instance.sendToServer("lo;j;i;FaZeGa;Groz;barbare;24;43");
    enterState(LOADING);
   }
   if((state==LOADING && frames==1)||(state==PRINCIPAL && frames==100)){
    Image shot=new Image(c.getWidth(),c.getHeight());g.copyArea(shot,0,0);
    org.newdawn.slick.imageout.ImageOut.write(shot,"png",System.getProperty("test.output")+"/"+(state==LOADING?"latest-loading":"latest-gameplay")+".png",false);
   }
   if(state==PRINCIPAL){
    com.client.map.managers.MapManager mm=com.client.map.managers.MapManager.instance;
    Tile[][] grid=mm.getEntire_map().getGrille();
    if(frames==1){
     if(!grid[15][36].isCollidable())throw new RuntimeException("Barrel not solid");
     MainJoueur.instance.setPos_real(grid[15][37].getPos_real_barycentre());
     System.out.println("BARREL_TEST_START "+MainJoueur.instance.getPos_real());
    }
    if(frames<150){MainJoueur.instance.setOrientation(Orientation.HAUT);MainJoueur.instance.moveKey();}
    if(mm.getTileReal(MainJoueur.instance.getPos_real()).isCollidable())throw new RuntimeException("Entered solid tile");
    if(frames==150){
     if(MainJoueur.instance.getPos_real().y>=1520)throw new RuntimeException("No movement toward barrel");
     System.out.println("BARREL_BLOCKED position="+MainJoueur.instance.getPos_real());
     MainJoueur.instance.setPos_real(grid[9][13].getPos_real_barycentre());
     NetworkManager.instance.sendToServer("s;pos;"+MainJoueur.instance.getPos_real().x+";"+MainJoueur.instance.getPos_real().y+";h");
    }
    if(frames==250){
     boolean found=false;
     for(PNJ p:com.client.entities.managers.EntitiesManager.instance.getPnjs_manager().getPnjs())if(p.getNom().equals("Well Caume"))found=true;
     if(!found)throw new RuntimeException("NPC not loaded after position sync");
     System.out.println("VERIFIED_NPC_LOADED_AFTER_POSITION_SYNC");c.exit();
    }
   }
  }catch(Exception e){e.printStackTrace();System.exit(2);}
 }
 public static void main(String[] a) throws SlickException {
  AppGameContainer app=new AppGameContainer(new RecoverySmokeTest());app.setDisplayMode(1000,680,false);app.setTargetFrameRate(30);app.setAlwaysRender(true);app.setUpdateOnlyWhenVisible(false);app.start();
 }
}
