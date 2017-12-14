
#include <iostream>
#include <cctype>
#include <cassert>
#include <string>

// for istringstream
#include <sstream>

using namespace std;

//*************************************************************************

struct Student {
  string name;
  int data;

  Student(const string &userName);
  int getScore() const;
  void addScore(int score);
  string getName() const;
  void foo4(const Student * & s);
};

Student::Student(const string &userName){
  name = userName;
  data = 0;
}

int Student::getScore() const{
  return data;
}

void Student::addScore(int score){
  data += score;
}

string Student::getName() const{
  return name;
}

void foo(Student *s){

    s->addScore(15);
    s = NULL;
}

void bar(Student * & t){

    t->addScore(25);
    t = NULL;
}

/* a little test program */

int main ()
{

  Student *joe = new Student("Joe");
  foo(joe);
  cout<< joe->getScore() << endl;

  Student * sal = new Student("Sal");
  bar(sal);
  cout << sal->getScore() << endl;

  return 0;
}



