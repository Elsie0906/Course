// Name: Yin-Hsia Yen
// USC NetID: yinhsiay
// CSCI 455 PA5
// Fall 2017

/*
 * grades.cpp
 * A program to test the Table class.
 * How to run it:
 *      grades [hashSize]
 * 
 * the optional argument hashSize is the size of hash table to use.
 * if it's not given, the program uses default size (Table::HASH_SIZE)
 *
 */

#include "Table.h"

// cstdlib needed for call to atoi
#include <cstdlib>
#include <string>

/*   
* string is not allowed to use in a switch statement (c/c++)
* Assign a number to a command(string), then we can use this enum in a switch statement
*/

enum string_code{
  eInsert = 0,
  eChange = 1,
  eLookup = 2,
  eRemove = 3,
  ePrint = 4,
  eSize = 5,
  eStats = 6,
  eHelp = 7,
  eQuit = 8,
  eError
};

/*
* Transit func for <command,number>
* @param inString
*     cmd from user input
* @return 
*     a corresponding number defined by our enum
*/

string_code hashit(string const &inString){
  if(inString == "insert")  return eInsert;
  else if(inString == "change") return eChange;
  else if(inString == "lookup") return eLookup;
  else if(inString == "remove") return eRemove;
  else if(inString == "print")  return ePrint;
  else if(inString == "size")   return eSize;
  else if(inString == "stats")  return eStats;
  else if(inString == "help")   return eHelp;
  else if(inString == "quit")   return eQuit;
  else  return eError;
}

/*
* a while loop to receive all cmd from user; exist the program until user enter "quit"
* @param grades    grades table(hash table)
*/
void runCmd(Table *grades);

/* 
* Handle all cmd in this func(operated by a switch statement)
* @param grades    grades table(hash table)
* @param cmd       command 
*/
void cmdList(Table *grades, string cmd);

/* 
* operating insert command
* e.g. insert Jane 100
* @param grades    grades table(hash table) 
*/
void cmdInsert(Table *grades);

/* 
* operating change command
* e.g. change Jane 95 
* @param grades    grades table(hash table)
*/
void cmdChange(Table *grades);

/*
* operating lookup command
* e.g. lookup Jane
* @param grades    grades table(hash table)
*/
void cmdLookup(Table *grades);

/*
* operating remove command
* e.g. remove Jane
* @param grades    grades table(hash table)
*/
void cmdRemove(Table *grades);

/*
* Prints out the number of entries in the table
* @param grades    grades table(hash table)    
*/
void cmdSize(Table *grades);

/*
* Prints out a brief command summary
*/
void cmdSummary();

/*
* create a hash table; use default array size(Table::HASH_SIZE) if the array size is not given
* prints out the hashStats() for that empty table, and then call runCmd()
* 
* local variable:
*     grades    A dynammic array is allocated
*/

int main(int argc, char * argv[]) {

  // gets the hash table size from the command line

  int hashSize = Table::HASH_SIZE;

  Table * grades;  // Table is dynamically allocated below, so we can call
                   // different constructors depending on input from the user.

  if (argc > 1) {
    hashSize = atoi(argv[1]);  // atoi converts c-string to int

    if (hashSize < 1) {
      cout << "Command line argument (hashSize) must be a positive number" << endl;
      return 1;
    }
    grades = new Table(hashSize);
  }
  else {   // no command line args given -- use default table size
    grades = new Table();
  }

  grades->hashStats(cout);

  runCmd(grades);

  return 0;
}

/* the program repeatedly reads and executes commands from the user until the user enters the quit command */

void runCmd(Table *grades){

  string cmd = "";

  do{
    cout << "cmd> ";

    cin >> cmd;

    cmdList(grades, cmd);

  }while(cmd != "quit");

}

/* 
* all cmd are seperatedly operating in this switch statement 
* error checking:
*    print out "ERROR: invalid command", and the command summary
*/

void cmdList(Table *grades, string cmd){

  switch(hashit(cmd)){
    case eInsert:
        cmdInsert(grades);
        break;        
    case eChange:
        cmdChange(grades);        
        break;
    case eLookup:
        cmdLookup(grades);
        break;
    case eRemove:
        cmdRemove(grades); 
        break;
    case ePrint:
        grades->printAll();
        break;
    case eSize:
        cmdSize(grades);
        break;
    case eStats:
        grades->hashStats(cout);
        break;
    case eHelp:
        cmdSummary();
        break;
    case eQuit:
        cout << "Exits the program" << endl;
        break;
    default:
        {
          cout << "ERROR: invalid command" << endl;
          cmdSummary();
        }
        break;      
    }    
}

/*
* Prints out the number of entries in the table
* @param grades    grades table(hash table)    
*/
void cmdSize(Table *grades){

    int num = grades->numEntries();

    cout << num << " entries in the table" << endl;

}

/*
* operating remove command
* e.g. remove Jane
* @param grades    grades table(hash table)
*/
void cmdRemove(Table *grades){
    string name;
    cin >> name;
    bool reVal = grades->remove(name);

    if( reVal == false){
        cout << "ERROR: " << name << " is not in the table" << endl; 
    }    
}

/*
* operating lookup command
* e.g. lookup Jane
* @param grades    grades table(hash table)
*/
void cmdLookup(Table *grades){
    
    string name;      
    cin >> name;
    int *score = grades->lookup(name);

    if( score != NULL){
        cout << name << " got score " << *score << endl;
    }  
    else{
        cout << name << " is not in the table" << endl;
    }
}

/* 
* operating change command
* e.g. change Jane 95 
* @param grades    grades table(hash table)
*/
void cmdChange(Table *grades){
    
    string name;
    int score = 0;

    cin >> name >> score;
    
    int *oriScore = grades->lookup(name);
    
    if( oriScore != NULL){
        *oriScore = score;
    }
    else{
        cout << "score unchanged because " << name << " is not in the table"<< endl;
    }
}

/* 
* operating insert command
* e.g. insert Jane 100
* @param grades    grades table(hash table) 
*/
void cmdInsert(Table *grades){

    string name;
    int score = 0;

    cin >> name >> score;
    bool reVal = grades->insert(name, score);

    if( reVal == false){
        cout << name << "already in the table" << endl;
    }  
}

/*
* A list of command
*/

void cmdSummary(){
    cout << "insert name score:    ex. insert Jane 100" << endl;
    cout << "change name newscore: ex. change Jane 95" << endl;
    cout << "lookup name:          ex. lookup Jane" << endl;
    cout << "remove name:          ex. remove Jane" << endl;
    cout << "print:                Prints out all names and scores in the table" << endl;
    cout << "size:                 Prints out the number of entries in the table" << endl;
    cout << "stats:                Prints out statistics about the hash table at this point" << endl;
    cout << "help:                 Prints out a brief command summary" << endl;
    cout << "quit:                 Exits the program" << endl;
}
