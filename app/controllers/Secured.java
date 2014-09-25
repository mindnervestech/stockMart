package controllers;

import models.User;
import play.mvc.Http;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;

public class Secured  extends Security.Authenticator{

	  @Override
	    public String getUsername(Context ctx) {


          Http.Session session = ctx.session();
          if(session.get("email") == null)
			{
			 return null; 
			}
//	    	if(Merchant.findByUserName(ctx.session().get("email"))!=null){
          try {
              if(User.findByEmail(ctx.session().get("email"))!=null){
                  return ctx.session().get("email");
              }
          } catch (Exception e) {
              e.printStackTrace();
          }
          return null;
	    }

	    @Override
	    public Result onUnauthorized(Context ctx) {
	        return redirect(routes.Application.index());
	    }
}
