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

		cout << "Please enter some integers (enter -1 to end):\n";
		
		do
		{

			cin >> myint;
			if( myint != -1)
				myvector.push_back(myint);

		}while(myint != -1);

		//cout << "myvector stores " << int(myvector.size()) << " numbers.\n";

		return myvector;
	}

	void printVals(vector<int> v){

		for(int i=0; i<v.size(); i++){
			cout << v[i] << " ";
		}
	}

	private:


}; /*End of class Solution */

int main()
{
	Solution sol;

	sol.printVals(sol.readVals());

	return 0;
}
