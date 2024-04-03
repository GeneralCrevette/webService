JERSEY 

lancer une applicatiton jersey 

mvn archetype:generate -DarchetypeGroupId=org.glassfish.jersey.archetypes \ 
-DarchetypeArtifactId=jersey-quickstart-grizzly2 -DarchetypeVersion=3.1.5

EXEMPLE DE CLASSE EN JERSEY ( c'est comme java )

public class Livre {
    public String Titre;
    public String Description;

    public Livre(String titre, String description){
        this.Titre = titre;
        this.Description = description;
    }

}

Dans le controller (en gros qu'on API ici)

@Path("address")

public List<Livre> livres = new ArrayList<>();
public apiBook() {
    livres.add(new Livre("Le Seigneur des Anneaux", "Un roman épique de J.R.R. Tolkien."));
    livres.add(new Livre("1984", "Un roman de George Orwell décrivant une dystopie totalitaire."));
    livres.add(new Livre("Harry Potter à l'école des sorciers", "Le premier tome de la série Harry Potter de J.K. Rowling."));
}


@Path("address")
@VERBS
@Produces("text/html")
public String getBooks(){
    try {
        StringBuilder html = new StringBuilder();
        html.append("<h1>List Of Books </h1>")
        html.append("<ul>")
        for(Livre livre : livres) {
            html.append("<p>").append(livre.Titre).append("</p">)
            html.append("<p>").append(livre.Description).append("</p>")
        }
        html.append("</ul>")
        return html.toString()
    } catch(Exception e){
        throw new WebApplicationException(500)
    }
}

@Path("/addLivre")
@POST
@Consumes("text/html")
public boolean addLivre(@QueryParam("titre") String titre, @QueryParam("description") String description ) {
    try {
        Livre nouveauLivre = new Livre(titre,description);
        livres.add(nouveauLivre);
        return true;
    }
    catch(Exception e){
        throw new WebApplicationException(500);
    }
}


Client en JERSEY 

public static void main(String[] args) throws IOException {
    Client client = ClientBuilder.newClient();
    WebTarget webTarget = client.target("http://localhost:8080/apiBook");
    WebTarget helloworldWebTarget = webTarget.path("/addLivre/");
    WebTarget helloworldWebTargetWithPathParam = helloworldWebTarget
            .queryParam("texte", "valeur_texte")
            .queryParam("description", "valeur_description");
    Invocation.Builder invocationBuilder = helloworldWebTargetWithPathParam.request();
    Response response = invocationBuilder.post(null);
    System.out.println("Response status: " + response.getStatus());
    System.out.println("Response body: " + response.readEntity(String.class));

    response.close();

}
