Créer un projet composer


curl -sS https://getcomposer.org/installer | php

php composer.phar require slim/psr7

php composer.phar require slim/slim:"4.*"

créer un répertoir public et un fichier index.html

classe

<?php

class Employee
{
    public int $id;
    public string $name;

    /**
     * @param int $id
     * @param string $name
     */
    public function __construct(int $id, string $name)
    {
        $this->id = $id;
        $this->name = $name;
    }


}


index.html 


<?php
use Psr\Http\Message\ResponseInterface as Response;
use Psr\Http\Message\ServerRequestInterface as Request;
use Slim\Factory\AppFactory;

$loader = require __DIR__ . '/../vendor/autoload.php';
require __DIR__ . '/Employee.php';
//for namespace BL PSR4 here namespace ; dir
$loader->addPsr4('BL\\', __DIR__);
$app = AppFactory::create();

$app->get('/', function (Request $request, Response $response, $args) {
    $response->getBody()->write("Hello world!");
    return $response;
});

$employees = [
    ['id' => 1, 'name' => 'John Doe'],
    ['id' => 2, 'name' => 'Jane Smith']
];

$data = [
    new Employee(1,"Jane"),
    new Employee(2,"Oscour")
];

$app->get('/employees', function (Request $request, Response $response, $args) use ($data) {
    try {
        $employeeNames = [];
        foreach ($data as $emp) {
            $employeeNames[] = $emp->name;
        }
        $jsonResponse = json_encode($employeeNames);
        $response->getBody()->write($jsonResponse);
        return $response->withHeader('Content-type', 'application/json')->withStatus(200);
    } catch (Exception $e) {
        throw new HttpInternalServerErrorException();
    }
});

$app->get('/employees/{id}', function (Request $request, Response $response, $args) use ($data) {
    try {
        $id = $args['id'];
        $employee = null;
        foreach ($data as $emp) {
            if ($emp->id == $id) {
                $employee = $emp;
                break;
            }
        }
        if ($employee === null) {
            return $response->withStatus(404)->getBody()->write("Employee not found");
        }
        $payload = json_encode($employee);
        $response->getBody()->write($payload);
        return $response->withHeader('Content-type', 'application/json')->withStatus(200);
    } catch (Exception $e) {
        throw new HttpInternalServerErrorException();
    }
});

$app->post('/employees', function (Request $request, Response $response, $args) use ($data) {
    try {
        $db = $request->getParsedBody();
        if (!isset($db['name'])) {
            return $response->withStatus(400)->getBody()->write("Missing required fields");
        }
        $newEmployee = new Employee(count($data)+1,$db['name']);
        array_push($data,$newEmployee);
        return $response->withHeader('Content-type', 'application/json')->withStatus(200);
        } catch (Exception $e) {
            throw new HttpInternalServerErrorException();
        }
});




$app->run();