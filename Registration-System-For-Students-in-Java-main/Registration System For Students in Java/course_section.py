import random
from course import Course
from mandatory_course import MandatoryCourse

from schedule import Schedule

class CourseSection(object):
    
    def __init__(self, course: Course):
        self.course = course
        self.course_program = []
        self.students = []
        
        
        
    def set_course_program(self):
        section_hours = self.course.total_hours()
        
        for i in range(section_hours):
            rand_hour = random.randint(0, Schedule.HOURS - 1)
            rand_day = random.randint(0, Schedule.DAYS - 1)
            
            if not self.course_program[rand_hour][rand_day] and not self.collides_with_same_semester(rand_hour, rand_day):
                self.course_program[rand_hour][rand_day] = True
            else:
                i -= 1        
                
                
    def collides_with_same_semester(self, rand_hour, rand_day):
        if not isinstance(self.course, MandatoryCourse):
            return False
        
        mandatory_courses = RegistrationSystem.mandatory_courses
        return any(self.course.semester_num == c.semester_number and  c.course_section.course_program()[rand_hour][rand_day] 
                   for c in mandatory_courses)
        
    
    def is_full(self):
        return len(self.students) >= self.course.quota