SPRINGBOOT SANS HATEOS 

Classe 

@Data
@Entity
public class Account{

    @Id
    @GeneratedValue(strategy= GenerationType.Auto)
    private long account_id;

    @Column(name="somme")
    private int somme;

    @Column(name="risk")
    private String risk;

    public Account(){ }

    public Account( int somme, String risk) {
        this.somme = somme;
        this.risk = risk;
    }
}

Si pas de repository dans le controller on fait une list et on add des objets

repository
public interface AccountRepository extends CrudRepository <Account,Long> {
    Account findById(long id);
}


Controller 

@RestController
@RequestMapping("/account")
public class AccountController {

    private final AccountRepository repository;


    public AccountController(AccountRepository repository) {
        this.repository = repository;
        repository.save(new Account(1500,"high"));
        repository.save(new Account(10000,"low"));
        repository.save(new Account(10,"high"));
    }

    @RequestMapping(value = "/Accounts", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody Iterable<Account> accounts() {
        try {
            return repository.findAll();
        } catch (Exception e){
            throw new AccountNotFoundException(e.getMessage());
        }

    }

    @GetMapping(value = "/Accounts/{account_id}", produces = "application/json")
    public @ResponseBody String account(@PathVariable("account_id") long account_id) {
        try {
            Account account = repository.findById(account_id);
            return account.getRisk();
        } catch (Exception e){
            throw new AccountNotFoundException(e.getMessage());
        }

    }
    @RequestMapping(value="/Accounts",method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public @ResponseBody Account add(@RequestBody Account a)
    {
        try{
            repository.save(a);
            return a;
        }
        catch (Exception e)
        { throw new AccountForbiddenException(e.getMessage()); }
    }

}

Gestion D'erreur'

@ControllerAdvice
public class BookAdvice {
    @ResponseBody
    @ExceptionHandler(BookNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
     String bookNotFoundHandler(BookNotFoundException ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(BookForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    String forbiddenHandler(BookForbiddenException ex) {
        return ex.getMessage();
    }
}

public class BookNotFoundException extends RuntimeEXception {
    public BookNotFoundException(String message){
        super(message)
    }
}

Avec HAteos c'est' le controller qui change 


 @RequestMapping(value="/students", method = RequestMethod.GET, produces="application/json")
    public CollectionModel<EntityModel<Student>> students(){
        try{
            List<EntityModel<Student>> studentEntities = new ArrayList<>();
            Iterable<Student> students = repository.findAll();
            students.forEach(student -> {
                studentEntities.add(EntityModel.of(student,
                        linkTo(methodOn(StudentController.class).student(student.getIdStudent())).withSelfRel(),
                        linkTo(methodOn(StudentController.class).students()).withRel("students")));
            });
            return CollectionModel.of(studentEntities,linkTo(methodOn(StudentController.class).students()).withSelfRel());
        } catch (Exception e){
            throw new StudentNotFoundException(e.getMessage());
        }
    }

    @GetMapping(value = "/students/{student_id}", produces = "application/json")
    public EntityModel<Student> student(@PathVariable("student_id") long student_id) {
        try {
            Student student = repository.findById(student_id);
            return EntityModel.of(student,
                    linkTo(methodOn(StudentController.class).student(student_id)).withSelfRel(),
                    linkTo(methodOn(StudentController.class).students()).withRel("students"));
        } catch (Exception e){
            throw new StudentNotFoundException(e.getMessage());
        }
    }

    @PostMapping(value = "/students", consumes = "application/json", produces = "application/json")
    public EntityModel<Student> addStudent(@RequestBody Student student) {
        try {
            Student savedStudent = repository.save(student);
            EntityModel<Student> ressource = EntityModel.of(savedStudent,
                    linkTo(methodOn(StudentController.class).student(savedStudent.getIdStudent())).withSelfRel(),
                    linkTo(methodOn(StudentController.class).students()).withRel("students"));
            return ressource;
        } catch (Exception e) {
            throw new StudentForbiddenException(e.getMessage());
        }

    }

    @DeleteMapping(value = "/students/{student_id}")
    public ResponseEntity<Student> deleteStudent(@PathVariable("student_id") long student_id) {
        try {
            if (repository.existsById(student_id)) {
                repository.deleteById(student_id);
                return ResponseEntity.ok().build();
            } else {
                throw new StudentNotFoundException("Student with ID " + student_id + " not found");
            }
        } catch (Exception e) {
            throw new StudentForbiddenException(e.getMessage());
        }
    }


    @PutMapping(value = "/students/{student_id}")
    public ResponseEntity<EntityModel<Student>> updateStudent(@PathVariable("student_id") long student_id, @RequestBody Student updatedStudent) {
        try {
            Student existingStudent = repository.findById(student_id);
            if (existingStudent != null) {
                existingStudent.setName(updatedStudent.getName());
                existingStudent.setPhoto(updatedStudent.getPhoto());
                existingStudent.setGroupId(updatedStudent.getGroupId());
                Student savedStudent = repository.save(existingStudent);
                return ResponseEntity.ok(EntityModel.of(savedStudent,
                        WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(GroupStudentController.class).group(student_id)).withSelfRel(),
                        linkTo(methodOn(GroupStudentController.class).groups()).withRel("groups")));
            } else {
                throw new GroupNotFoundException("Group with ID " + student_id + " not found");
            }
        } catch (Exception e) {
            throw new GroupForbiddenException(e.getMessage());
        }
    }
