const API_URL = 'http://localhost:8080/api';

let students = [];
let courses = [];
let materials = [];

// Initialize
document.addEventListener('DOMContentLoaded', () => {
    loadStudents();
    loadCourses();
    loadMaterials();
    setupEventListeners();
});

function setupEventListeners() {
    document.getElementById('studentFormElement').addEventListener('submit', handleStudentSubmit);
    document.getElementById('courseFormElement').addEventListener('submit', handleCourseSubmit);
    document.getElementById('materialFormElement').addEventListener('submit', handleMaterialSubmit);
}

// Tab Management
function showTab(tabName) {
    document.querySelectorAll('.tab-content').forEach(tab => tab.classList.remove('active'));
    document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
    
    document.getElementById(tabName).classList.add('active');
    event.target.classList.add('active');
}

// Student Management
function showStudentForm() {
    document.getElementById('studentForm').style.display = 'block';
}

function hideStudentForm() {
    document.getElementById('studentForm').style.display = 'none';
    document.getElementById('studentFormElement').reset();
}

async function loadStudents() {
    try {
        const response = await fetch(`${API_URL}/students`);
        students = await response.json();
        displayStudents();
        updateStudentDropdowns();
    } catch (error) {
        console.error('Error loading students:', error);
    }
}

function displayStudents() {
    const container = document.getElementById('studentsList');
    container.innerHTML = students.map(student => `
        <div class="card">
            <h3>${student.name}</h3>
            <p><strong>Email:</strong> ${student.email}</p>
            <p><strong>Enrollment ID:</strong> ${student.enrollmentId}</p>
            <p><strong>Courses:</strong> ${student.courses.length}</p>
            <div class="card-actions">
                <button class="btn btn-danger" onclick="deleteStudent('${student.id}')">Delete</button>
            </div>
        </div>
    `).join('');
}

async function handleStudentSubmit(e) {
    e.preventDefault();
    
    const studentData = {
        name: document.getElementById('studentName').value,
        email: document.getElementById('studentEmail').value,
        enrollmentId: document.getElementById('studentEnrollment').value,
        courses: []
    };
    
    try {
        await fetch(`${API_URL}/students`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(studentData)
        });
        
        hideStudentForm();
        loadStudents();
    } catch (error) {
        console.error('Error creating student:', error);
    }
}

async function deleteStudent(id) {
    if (!confirm('Are you sure you want to delete this student?')) return;
    
    try {
        await fetch(`${API_URL}/students/${id}`, { method: 'DELETE' });
        loadStudents();
    } catch (error) {
        console.error('Error deleting student:', error);
    }
}

// Course Management
function showCourseForm() {
    document.getElementById('courseForm').style.display = 'block';
}

function hideCourseForm() {
    document.getElementById('courseForm').style.display = 'none';
    document.getElementById('courseFormElement').reset();
}

async function loadCourses() {
    try {
        const response = await fetch(`${API_URL}/courses`);
        courses = await response.json();
        displayCourses();
        updateCourseDropdowns();
    } catch (error) {
        console.error('Error loading courses:', error);
    }
}

function displayCourses() {
    const container = document.getElementById('coursesList');
    container.innerHTML = courses.map(course => `
        <div class="card">
            <h3>${course.courseName}</h3>
            <p><strong>Code:</strong> ${course.courseCode}</p>
            <p><strong>Credits:</strong> ${course.credits}</p>
            <p><strong>Description:</strong> ${course.description}</p>
            <div class="card-actions">
                <button class="btn btn-danger" onclick="deleteCourse('${course.id}')">Delete</button>
            </div>
        </div>
    `).join('');
}

async function handleCourseSubmit(e) {
    e.preventDefault();
    
    const courseData = {
        courseName: document.getElementById('courseName').value,
        courseCode: document.getElementById('courseCode').value,
        description: document.getElementById('courseDescription').value,
        credits: parseInt(document.getElementById('courseCredits').value)
    };
    
    try {
        await fetch(`${API_URL}/courses`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(courseData)
        });
        
        hideCourseForm();
        loadCourses();
    } catch (error) {
        console.error('Error creating course:', error);
    }
}

async function deleteCourse(id) {
    if (!confirm('Are you sure you want to delete this course?')) return;
    
    try {
        await fetch(`${API_URL}/courses/${id}`, { method: 'DELETE' });
        loadCourses();
    } catch (error) {
        console.error('Error deleting course:', error);
    }
}

// Study Material Management
async function loadMaterials() {
    try {
        const response = await fetch(`${API_URL}/materials/student/all`);
        // Since we don't have an "all" endpoint, we'll load per student
        materials = [];
        for (const student of students) {
            const res = await fetch(`${API_URL}/materials/student/${student.id}`);
            const studentMaterials = await res.json();
            materials.push(...studentMaterials);
        }
        displayMaterials();
    } catch (error) {
        console.error('Error loading materials:', error);
        displayMaterials();
    }
}

function displayMaterials() {
    const container = document.getElementById('materialsList');
    
    if (materials.length === 0) {
        container.innerHTML = '<p style="text-align: center; color: #718096;">No study materials uploaded yet.</p>';
        return;
    }
    
    container.innerHTML = materials.map(material => {
        const student = students.find(s => s.id === material.studentId);
        const course = courses.find(c => c.id === material.courseId);
        const uploadDate = new Date(material.uploadDate).toLocaleDateString();
        const fileSize = (material.fileSize / 1024).toFixed(2);
        
        const thumbnailUrl = material.thumbnailId 
            ? `${API_URL}/materials/thumbnail/${material.id}`
            : null;
        
        return `
            <div class="material-item">
                <div class="material-thumbnail">
                    ${thumbnailUrl 
                        ? `<img src="${thumbnailUrl}" alt="PDF Preview" onerror="this.parentElement.innerHTML='<span class=\\'material-thumbnail-placeholder\\'>📄</span>'">` 
                        : '<span class="material-thumbnail-placeholder">📄</span>'
                    }
                </div>
                <div class="material-info">
                    <h4>📄 ${material.fileName}</h4>
                    <p><span class="badge">Student:</span> ${student ? student.name : 'Unknown'}</p>
                    <p><span class="badge">Course:</span> ${course ? course.courseName : 'Unknown'}</p>
                    <p><strong>Uploaded:</strong> ${uploadDate} | <strong>Size:</strong> ${fileSize} KB</p>
                </div>
                <div class="material-actions">
                    <button class="btn btn-primary" onclick="downloadMaterial('${material.id}', '${material.fileName}')">Download</button>
                    <button class="btn btn-danger" onclick="deleteMaterial('${material.id}')">Delete</button>
                </div>
            </div>
        `;
    }).join('');
}

async function handleMaterialSubmit(e) {
    e.preventDefault();
    
    const formData = new FormData();
    const fileInput = document.getElementById('materialFile');
    const studentId = document.getElementById('materialStudent').value;
    const courseId = document.getElementById('materialCourse').value;
    
    if (!fileInput.files[0]) {
        alert('Please select a PDF file');
        return;
    }
    
    formData.append('file', fileInput.files[0]);
    formData.append('studentId', studentId);
    formData.append('courseId', courseId);
    
    try {
        await fetch(`${API_URL}/materials/upload`, {
            method: 'POST',
            body: formData
        });
        
        document.getElementById('materialFormElement').reset();
        loadMaterials();
        alert('Material uploaded successfully!');
    } catch (error) {
        console.error('Error uploading material:', error);
        alert('Error uploading material');
    }
}

async function downloadMaterial(id, fileName) {
    try {
        const response = await fetch(`${API_URL}/materials/download/${id}`);
        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = fileName;
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);
    } catch (error) {
        console.error('Error downloading material:', error);
    }
}

async function deleteMaterial(id) {
    if (!confirm('Are you sure you want to delete this material?')) return;
    
    try {
        await fetch(`${API_URL}/materials/${id}`, { method: 'DELETE' });
        loadMaterials();
    } catch (error) {
        console.error('Error deleting material:', error);
    }
}

function updateStudentDropdowns() {
    const selects = [
        document.getElementById('materialStudent'),
        document.getElementById('filterStudent')
    ];
    
    selects.forEach(select => {
        const currentValue = select.value;
        select.innerHTML = '<option value="">Select Student</option>' +
            students.map(s => `<option value="${s.id}">${s.name}</option>`).join('');
        if (currentValue) select.value = currentValue;
    });
}

function updateCourseDropdowns() {
    const selects = [
        document.getElementById('materialCourse'),
        document.getElementById('filterCourse')
    ];
    
    selects.forEach(select => {
        const currentValue = select.value;
        select.innerHTML = '<option value="">Select Course</option>' +
            courses.map(c => `<option value="${c.id}">${c.courseName}</option>`).join('');
        if (currentValue) select.value = currentValue;
    });
}

function filterMaterials() {
    const studentId = document.getElementById('filterStudent').value;
    const courseId = document.getElementById('filterCourse').value;
    
    // Implement filtering logic here if needed
    loadMaterials();
}