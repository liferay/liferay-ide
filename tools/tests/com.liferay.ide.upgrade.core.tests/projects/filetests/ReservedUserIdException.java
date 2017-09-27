import com.liferay.portal.ReservedUserIdException;

public class ReservedUserIdExceptionCatch{
	public void catchTheException(){
		try{
			reserveUserId();
		}catch (ReservedUserIdException nsre) {
			
		}
	}
	
}