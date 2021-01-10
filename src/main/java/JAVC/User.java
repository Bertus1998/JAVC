package JAVC;

public class User {
    private String name;
   private String email;
   private String password1;
   private String password2;
    User(String name, String password1)
    {
        this.name= name;
        this.password1 = password1;

    }
    User(String name, String password1, String password2, String email)
    {
        this.name = name;
        this.password1 =password1;
        this.password2 = password2;
        this.email = email;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasword1() {
        return password1;
    }

    public void setPasword1(String pasword1) {
        this.password1 = pasword1;
    }

    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }


}
