CLIENT GUZZLE

composer require guzzlehttp/guzzle:^7.0.



<?php
use GuzzleHttp\Client;

$proxy1=''; //http://193.49.118.36:8080
$proxy2=''; //http://193.49.118.36:8080
$base_uri='http://localhost:8888';

require __DIR__ . '/../vendor/autoload.php';
try{
    $client = new GuzzleHttp\Client(['base_uri' => $base_uri]);
    $response = $client->request('GET', '/employees',['proxy' => ['http' => $proxy1, 'https'=>$proxy2]]);
    $code = $response->getStatusCode();
    $body = $response->getBody();
    echo $code;
    echo "\n";
    echo (string) $body;

    $response = $client->request('POST', '/employees', [
        'proxy' => ['http' => $proxy1, 'https' => $proxy2],
        'form_params' => [
            'name' => 'Frederic'
        ]
    ]);
    $code = $response->getStatusCode();
    $body = $response->getBody();
    echo $code;
    echo "\n";
    echo (string) $body;


}
catch (ConnectException $ee){ echo "error in URL";}
catch (ClientException $e) {
    echo Psr7\Message::toString($e->getRequest());
    echo Psr7\Message::toString($e->getResponse());
}