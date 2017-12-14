/*
 * 1. ...
 */

#include <iostream>
#include <vector>
#include <fstream>
#include <string>
using namespace std;

class Solution 
{
	public:

	vector<int> readVals(){

		vector<int> myvector;
		int myint = 0;

		ofstream myfile;
/*
		myfile.open ("example.txt");

		myfile << "3 -7 10 17 9 -17  17 21 18 7";

		myfile.close();
*/
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
/**
 * returns location of last instance of target in v or -1 if not found
 */
	int findlast(vector<int> v, int target){

		int find = -1;
		
		for(int i=0; i<v.size(); i++){
		
			//cout << "[DEBUG]" << v[i] ;
			if( v[i] == target){
				find = i;
			}

		}

		return find;

	}

    void testFindLast(vector<int> v, int target) {
        cout << "testFindLast function\n";
        cout << "test vector = ";
        printVals(v);
        cout << "test target = " << target << "\n";

        int reVal = findlast(v, target);

		if( reVal == -1){
			cout << "target not find! reVal: " << reVal << endl;
		}else{
			cout << "Find target at index: " << reVal << endl;
		}
    }	

}; /*End of class Solution */

int main(int argc, char *argv[])
{
	Solution sol;

	//cout << argv[0] << argv[1] << endl;

	vector<int> v1 = sol.readVals();

	cout << "original data:" << endl;

	sol.printVals(v1);

	vector<int> v2 = sol.valsGT0(v1);

	cout << "filtered data:" << endl;

	sol.printVals(v2);

	cout << "please enter a num for search: " << endl;

	int target;

	cin >> target;

	sol.testFindLast(v1,target);
	sol.testFindLast(v2,target);

	return 0;
}
