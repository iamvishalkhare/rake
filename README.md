# Rake - Java implementation of Rapid Automated Keywords extraction

Keywords extraction from a given paragraph of text is what this project does.

## Installing instructions
1. Clone the repo
2. mvn clean install
3. Open RakeAlgorithm.java in STS or eclipse
4. Right click on the file
5. Select Run as -> Java application


## Output of algorithm
rakefile.txt in resources folder contains an article written by me on "Angular Routing" - https://medium.com/@iamvishalkhare/angular-routing-navigation-bab9880ec4be

Following are the results along with the score after passing the text of the article mentioned above to the algorithm-

router module takes route path---21.5
angular project named myproject---15.333333
components called SearcherComponent---9.0
personally prefer communicating---9.0
SearcherComponent implements OnInit---9.0
application render SearcherComponent---8.5
LanderComponent implements OnInit---8.5
variable called dataToTransmit---7.5
route navigation---5.5
angular service---5.333333
private router---5.0
application loads---4.5
LanderComponent import---4.5
dataToTransmit variable---4.5
Routes variable---4.166667
routes variable---4.166667
Angular Router---4.0
Angular framework---4.0
routing flag---4.0
routes files---4.0
routes file---4.0
ts file---4.0
const routes---4.0
html file---4.0
lander component---4.0
function navigate---4.0
sibling routes---4.0
navigate function---4.0
Passing data---4.0
button click---4.0
form submission---4.0
form data---4.0
multiple ways---4.0
text field---4.0
input type---4.0
service DataTransmitter---4.0
typescript file---4.0
DataTransmitter service---4.0
private dataTransmitter---4.0
app navigates---4.0
real world---4.0

## Tweaking the algorithm
To test keyword extraction accuracy and efficiency just change the content of rakefile.txt to your own content. The algorithm will extract keywords from the text in rakefile.txt

One can also tweak the results by adding/removing special characters in loadPunctStopWord() function in RakeAlgorithm.java
as well as by adding/removing stopwords in rakestopwords.txt inside resources folder.
