# نظام إدارة الطلاب - دليل النظام الشامل

## 📋 نظرة عامة على المشروع

تم تطوير **نظام إدارة الطلاب المتقدم** بـ Java مع:
- ✅ نظام تحكم محدد للأدوار (Role-Based Access Control)
- ✅ نظام صلاحيات شامل (Permission System)
- ✅ معمارية OOP نظيفة وقابلة للصيانة
- ✅ تخزين البيانات بـ JSON
- ✅ واجهة رسومية حديثة بـ Swing

---

## 🔐 نظام الأدوار والصلاحيات

### 1. مدير النظام (System Admin) `ADMIN`

**السلطات الكاملة:** جميع الصلاحيات

#### الصلاحيات:
- ✅ إضافة/حذف/تعديل المستخدمين
- ✅ إدارة إعدادات النظام
- ✅ عرض التقارير المالية والأكاديمية
- ✅ عمل النسخ الاحتياطية واستعادتها
- ✅ عرض سجلات النشاطات
- ✅ **جميع صلاحيات الموظفين والمعلمين**

#### بيانات الدخول:
```
Username: admin
Password: admin
```

---

### 2. موظف الإدارة (Registrar/Staff) `STAFF`

**المسؤوليات:** البيانات الأساسية والتنظيم الإداري

#### الصلاحيات:
- ✅ إضافة/تعديل/حذف بيانات الطلاب
- ✅ إضافة/تعديل/حذف المواد الدراسية
- ✅ تسجيل الطلاب في المواد
- ✅ عرض الدرجات والحضور
- ✅ عرض التقارير الأكاديمية والمالية
- ❌ لا يستطيع رصد الدرجات مباشرة

#### بيانات الدخول:
```
Username: staff
Password: staff123
```

---

### 3. المعلم (Teacher/Instructor) `TEACHER`

**المسؤوليات:** الجانب الأكاديمي والطلاب الخاص به

#### الصلاحيات:
- ✅ عرض بيانات الطلاب الخاص به فقط
- ✅ رصد الدرجات للطلاب
- ✅ تسجيل الحضور والغياب
- ✅ عرض التقارير الأكاديمية
- ❌ لا يستطيع حذف أو تعديل بيانات الطلاب

#### بيانات الدخول:
```
Username: teacher
Password: teacher123
```

---

### 4. الطالب (Student) `STUDENT`

**صلاحيات محدودة:** عرض فقط

#### الصلاحيات:
- ✅ عرض درجاته الخاصة فقط
- ✅ معرفة معدله التراكمي (GPA)
- ✅ عرض حضوره وغيابه
- ❌ لا يمكنه تعديل أي بيانات

#### بيانات الدخول:
```
Username: student
Password: student123
```

---

## 📁 هيكل المشروع

```
Student_Management/
├── src/
│   ├── app/
│   │   └── App.java                 # نقطة الدخول الرئيسية
│   ├── database/
│   │   ├── DatabaseHelper.java      # (قديم - MySQL)
│   │   └── JSONDatabaseHelper.java  # ✨ جديد - JSON
│   ├── model/
│   │   ├── User.java                # ✨ محدّث مع Role
│   │   ├── Student.java
│   │   ├── Subject.java
│   │   ├── Enrollment.java
│   │   ├── Role.java                # ✨ جديد - Enum للأدوار
│   │   ├── Permission.java          # ✨ جديد - Enum للصلاحيات
│   │   └── RolePermissionMatrix.java # ✨ جديد - مدير الصلاحيات
│   ├── service/                     # ✨ طبقة الخدمات الجديدة
│   │   ├── AuthService.java         # خدمة المصادقة والتفويض
│   │   ├── StudentService.java      # خدمة إدارة الطلاب
│   │   ├── SubjectService.java      # خدمة إدارة المواد
│   │   └── AcademicService.java     # خدمة العمليات الأكاديمية
│   └── view/
│       ├── MainFrame.java           # (قديم)
│       └── MainFrameV2.java         # ✨ جديد - قائم على الأدوار
├── data/                            # ملفات البيانات JSON
│   ├── users.json
│   ├── students.json
│   ├── subjects.json
│   └── enrollments.json
├── bin/                             # ملفات .class المترجمة
└── lib/
    └── mysql-connector-j-9.0.0.jar
```

---

## 🏗️ معمارية النظام (OOP Architecture)

### نمط الطبقات (Layer Pattern):

```
┌─────────────────────────────────────┐
│      UI Layer (MainFrameV2)         │ ← واجهة المستخدم
├─────────────────────────────────────┤
│  Service Layer (Auth, Academic)     │ ← منطق الأعمال + التفويض
├─────────────────────────────────────┤
│   Data Access Layer (JSONDatabase)  │ ← إدارة البيانات
├─────────────────────────────────────┤
│  Model Layer (User, Student, etc)   │ ← كائنات البيانات
└─────────────────────────────────────┘
```

### مبادئ OOP المطبقة:

#### 1. **التغليف (Encapsulation)**
```java
// Example: User class with private fields
private int id;
private String username;
private Role role;
// Public getters/setters
public Role getRole() { return role; }
```

#### 2. **الوراثة (Inheritance)**
- `Role` و `Permission` - Enums للتصنيف
- فئات الخدمات مع واجهات موحدة

#### 3. **التعددية الشكلية (Polymorphism)**
```java
// Same interface for different services
StudentService.addStudent()
SubjectService.addSubject()
AcademicService.enrollStudent()
```

#### 4. **القطع والتجريد (Abstraction)**
```java
public interface IService {
    boolean add(Object entity);
    boolean delete(int id);
    List<Object> getAll();
}
```

---

## 🔒 نظام الصلاحيات التفصيلي

### جدول مقارنة صلاحيات الأدوار:

| الميزة | Admin | Staff | Teacher | Student |
|-------|-------|-------|---------|---------|
| **إدارة المستخدمين** | ✅ | ❌ | ❌ | ❌ |
| إضافة طالب | ✅ | ✅ | ❌ | ❌ |
| تعديل بيانات طالب | ✅ | ✅ | ❌ | ❌ |
| حذف طالب | ✅ | ✅ | ❌ | ❌ |
| **إدارة المواد** | ✅ | ✅ | ❌ | ❌ |
| **تسجيل الدرجات** | ✅ | ❌ | ✅ | ❌ |
| **تسجيل الحضور** | ✅ | ❌ | ✅ | ❌ |
| عرض درجاته | ✅ | ✅ | ✅ | ✅ |
| عرض GPA | ✅ | ✅ | ✅ | ✅ |
| **التقارير المالية** | ✅ | ✅ | ❌ | ❌ |
| **إدارة النظام** | ✅ | ❌ | ❌ | ❌ |

### قائمة الصلاحيات الكاملة:

```java
Permission.ADD_USER              // إضافة مستخدم
Permission.DELETE_USER           // حذف مستخدم
Permission.ADD_STUDENT           // إضافة طالب
Permission.EDIT_STUDENT          // تعديل بيانات الطالب
Permission.DELETE_STUDENT        // حذف طالب
Permission.VIEW_STUDENT          // عرض بيانات الطالب
Permission.ADD_SUBJECT           // إضافة مادة
Permission.EDIT_SUBJECT          // تعديل المادة
Permission.DELETE_SUBJECT        // حذف مادة
Permission.ADD_GRADE             // إضافة درجة
Permission.EDIT_GRADE            // تعديل درجة
Permission.VIEW_GRADES           // عرض الدرجات
Permission.ADD_ATTENDANCE        // تسجيل الحضور
Permission.VIEW_ATTENDANCE       // عرض الحضور
Permission.VIEW_FINANCIAL_REPORTS // عرض التقارير المالية
Permission.MANAGE_SETTINGS       // إدارة الإعدادات
Permission.BACKUP_DATA           // نسخ احتياطي
Permission.VIEW_LOGS             // عرض السجلات
```

---

## 🚀 كيفية الاستخدام

### تشغيل التطبيق:

```bash
# انتقل إلى مجلد المشروع
cd Student_Mangment

# قم بالترجمة (إذا لم تترجم)
javac -d bin -sourcepath src src/**/*.java

# شغل التطبيق
java -cp bin app.App
```

### شاشة تسجيل الدخول:

1. أدخل اسم المستخدم والكلمة المرور
2. سيتم فحص الصلاحيات فوراً
3. واجهة مخصصة حسب الدور

### الأدوار المختلفة:

#### بصفة Admin:
- تصفح جميع التبويبات
- إدارة كاملة للنظام

#### بصفة Staff:
- تبويب الطلاب (إضافة/تعديل/حذف)
- تبويب المواد (عرض فقط)
- تبويب تفاصيل الطلاب

#### بصفة Teacher:
- تبويب "My Classes" (الفصول الخاصة)
- تبويب Attendance

#### بصفة Student:
- تبويب "My Grades" (درجاتي فقط)

---

## 📊 جداول البيانات والهياكل

### جدول المستخدمين (users.json):
```json
{
  "users": [
    {
      "id": 1,
      "username": "admin",
      "password": "admin",
      "fullName": "System Administrator",
      "email": "admin@system.com",
      "role": "ADMIN",
      "active": true
    }
  ]
}
```

### جدول الطلاب (students.json):
```json
{
  "students": [
    {
      "id": 1,
      "name": "Ahmed",
      "email": "ahmed@example.com",
      "phone": "01008349894"
    }
  ]
}
```

### جدول المواد (subjects.json):
```json
{
  "subjects": [
    {
      "id": 1,
      "name": "Math",
      "code": "MATH101",
      "credits": 3
    }
  ]
}
```

### جدول التسجيلات (enrollments.json):
```json
{
  "enrollments": [
    {
      "id": 1,
      "studentId": 1,
      "subjectId": 1,
      "grade": 95.0,
      "subjectName": "Math",
      "subjectCode": "MATH101",
      "credits": 3
    }
  ]
}
```

---

## 🔧 العمليات الأساسية

### 1. إضافة طالب جديد:
- صلاحية مطلوبة: `Permission.ADD_STUDENT`
- من يمكنه: Admin, Staff
- الخطوات:
  1. انقر على "Add Student"
  2. أدخل الاسم والبريد الإلكتروني والهاتف
  3. انقر "Add"

### 2. رصد الدرجات:
- صلاحية مطلوبة: `Permission.ADD_GRADE`
- من يمكنه: Admin, Teacher
- الخطوات:
  1. اختر الطالب من Combo Box
  2. اختر المادة
  3. أدخل الدرجة (0-100)
  4. انقر "Enroll"

### 3. عرض المعدل التراكمي (GPA):
- صلاحية مطلوبة: `Permission.VIEW_GRADES`
- الصيغة: `GPA = Σ(Grade × Credits) / Σ(Credits)`

### 4. البحث عن أفضل طالب:
- صلاحية مطلوبة: `Permission.VIEW_ACADEMIC_REPORTS`
- يحسبها النظام بناءً على أعلى GPA

---

## 📝 رسالة الخطأ والتنبيهات

| الرسالة | المعنى | الحل |
|--------|--------|------|
| "Permission denied" | لا توجد صلاحية | غير اسم المستخدم |
| "Invalid username or password" | بيانات خاطئة | راجع بيانات الدخول |
| "User not authenticated" | لم تسجل دخول | سجل دخول أولاً |
| "Grade must be 0-100" | درجة خارج النطاق | صحح الدرجة |

---

## 🛠️ الميزات المستقبلية

- [ ] تسجيل الحضور الإلكتروني
- [ ] طباعة الشهادات
- [ ] نظام إرسال الرسائل
- [ ] تقارير متقدمة (PDF)
- [ ] واجهة ويب
- [ ] تطبيق موبايل
- [ ] التصدير إلى Excel
- [ ] النسخ الاحتياطي التلقائي

---

## 📞 معلومات التطوير

**اللغة:** Java 11+  
**واجهة المستخدم:** Swing  
**تخزين البيانات:** JSON  
**الهيكل المعماري:** Layered Pattern (N-Tier)  
**المعايير:** OOP Principles, SOLID

---

**آخر تحديث:** 25 مارس 2026  
**الإصدار:** 2.0 (مع نظام الأدوار والصلاحيات)  
**الحالة:** ✅ جاهز للاستخدام
