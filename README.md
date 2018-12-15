# scala-spark-cab-rides-predictions
A big data project for predicting prices of Uber/Lyft rides depending on the weather

## Contributors:
* Ravi Munde
* Karan Barai


### Project Structure :
* cab-price-connector - Data Collection Scala Project
* Databricks_Prediction_code.html - Anlysis and Spark Model(From Databricks.com)
* Cab_Price_Prediction.ipynb - Random Forrest Model in Python

### Data Model:

#### CabPrice
root</br>
 &nbsp;&nbsp;&nbsp;&nbsp;|- cab_type : String</br>
  &nbsp;&nbsp;&nbsp;&nbsp;|- destination : String</br>
  &nbsp;&nbsp;&nbsp;&nbsp;|- distance: Float</br>
  &nbsp;&nbsp;&nbsp;&nbsp;|- id: String</br>
  &nbsp;&nbsp;&nbsp;&nbsp;|- name: String</br>
  &nbsp;&nbsp;&nbsp;&nbsp;|- price: Float</br>
  &nbsp;&nbsp;&nbsp;&nbsp;|- product_id: String</br>
  &nbsp;&nbsp;&nbsp;&nbsp;|- source: String</br>
  &nbsp;&nbsp;&nbsp;&nbsp;|- surge_multiplier: String</br>
  &nbsp;&nbsp;&nbsp;&nbsp;|- time_stamp:Long</br>

#### Weather
root</br>
&nbsp;&nbsp;&nbsp;&nbsp;|- clouds : Float</br>
&nbsp;&nbsp;&nbsp;&nbsp;|- humidity : Float</br>
&nbsp;&nbsp;&nbsp;&nbsp;|- location : Float</br>
&nbsp;&nbsp;&nbsp;&nbsp;|- location : String</br>
&nbsp;&nbsp;&nbsp;&nbsp;|- temp : String</br>
&nbsp;&nbsp;&nbsp;&nbsp;|- pressure : Float</br>
&nbsp;&nbsp;&nbsp;&nbsp;|- wind : Float</br>

![Actor System](Actors.png)

Sample log of Actor System Running on EC2</br>

`INFO [CabRideSystem-akka.actor.default-dispatcher-2] a.DynamoActor - received 12 number of weather records`</br>
`INFO [CabRideSystem-akka.actor.default-dispatcher-4] a.DynamoActor - Weather Batch processed on DynamoDB`</br>
`INFO [CabRideSystem-akka.actor.default-dispatcher-9] a.DynamoActor - received 156 number of cab price records`</br>
`INFO [CabRideSystem-akka.actor.default-dispatcher-8] a.DynamoActor - Cab Prices Batch processed on DynamoDB`</br>
`INFO [CabRideSystem-akka.actor.default-dispatcher-7] a.Master - Cab ride data piped to Dynamo Actor`</br>
`INFO [CabRideSystem-akka.actor.default-dispatcher-13] a.DynamoActor - received 156 number of cab price records`</br>
`INFO [CabRideSystem-akka.actor.default-dispatcher-15] a.DynamoActor - Cab Prices Batch processed on DynamoDB`</br>


*NOTE: AWS Creditials need to be put in environment vairables*

### Model Evaluation Matrices
* Regression R_squared = 0.62
* Random Forrest Regression's Price Prediction Accuracy : 92.79 %
* Random Forrest Classification Surge Prediction Accuracy: 77.69 %</br>
  
Confusion Matrix for the Classifier</br>
<img src="ConfusionMatrix.PNG" alt="drawing" width="250"/>
