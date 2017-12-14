/*
 * 1. ...
 */

#include <iostream>
#include <vector>
#include <fstream>
using namespace std;

class Solution 
{
	public:

	vector<int> readVals(){

		vector<int> myvector;
		int myint = 0;
/*
		cout << "Please enter some integers (enter character to end):\n";
		
		do
		{

			cin >> myint;
			if( !cin.fail()){
				myvector.push_back(myint);	
			}

		}while(!cin.fail());

*/

		ofstream myfile;

		myfile.open ("example.txt");

		myfile << "3 -7 10 17 9 -17  17 21 18 7";

		myfile.close();

		ifstream vfile ("example.txt");

		if( !vfile.is_open()){
			cout << "cannot find file 1" << endl;
			return myvector;
		}
		else{
			cout << "file opened" << endl;
		}

		while( vfile >> myint){
			//cout << myint << " ";
			myvector.push_back(myint);
		}

		//cout << endl;

/*      open fail
		ifstream file("num.in");

		if( !file.is_open()){
			cout << "cannot find file" << endl;
			return myvector;
		}

		while( file >> myint )
		{
			//cout << "myint" << myint << endl;
			myvector.push_back(myint);			
		}	

		file.close(); 
*/
		return myvector;
	}

	void printVals(vector<int> v){

		for(int i=0; i<v.size(); i++){
			cout << v[i] << " ";
		}

		cout << endl;
	}

	vector<int> valsGT0(vector<int> v){
		// filter number < 0
		vector<int> myvector;

		for(int i=0; i<v.size(); i++){
		
			if( v[i] >= 0){
				myvector.push_back(v[i]);
			}

		}	

		return myvector;

	}


}; /*End of class Solution */

int main()
{
	Solution sol;

	vector<int> v1 = sol.readVals();

	cout << "original data:" << endl;

	sol.printVals(v1);

	vector<int> v2 = sol.valsGT0(v1);

	cout << "filtered data:" << endl;

	sol.printVals(v2);	

	return 0;
}
