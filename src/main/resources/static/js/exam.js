let questions = [];
let currentQuestionIndex = 0;
let answers = {};
let examId = null;
let attemptId = null;
let isSubmitting = false;

document.addEventListener('DOMContentLoaded', async () => {
    requireAuth('STUDENT');
    
    const urlParams = new URLSearchParams(window.location.search);
    examId = urlParams.get('id');
    
    if (!examId) {
        window.location.href = 'student.html';
        return;
    }

    try {
        const user = getUser();
        const attempt = await api.post(`/student/start-exam?examId=${examId}`);
        attemptId = attempt.id;
        
        const durationMins = attempt.exam.duration;
        if (durationMins) {
            let timeRemaining = durationMins * 60;
            const timerInterval = setInterval(() => {
                timeRemaining--;
                if (timeRemaining <= 0) {
                    clearInterval(timerInterval);
                    alert("Time is up! Submitting exam automatically.");
                    submitExam();
                } else {
                    const m = Math.floor(timeRemaining / 60);
                    const s = timeRemaining % 60;
                    document.getElementById('timer-display').innerText = `Time left: ${m}:${s.toString().padStart(2, '0')}`;
                }
            }, 1000);
        } else {
            document.getElementById('timer-display').innerText = 'Time left: Unlimited';
        }
        
        questions = await api.get(`/student/start-exam/${examId}`);
        if (questions.length === 0) {
            document.getElementById('exam-container').innerHTML = '<div class="card" style="text-align: center;"><p>No questions found for this exam.</p><button class="btn btn-primary" style="margin-top: 1rem;" onclick="window.location.href=\'student.html\'">Back</button></div>';
        } else {
            renderQuestion();
        }
    } catch (error) {
        console.error('Failed to load exam:', error);
        alert('Error loading exam');
    }
});

function renderQuestion() {
    const q = questions[currentQuestionIndex];
    const progress = ((currentQuestionIndex) / questions.length) * 100;
    
    document.getElementById('progress-fill').style.width = `${progress}%`;
    document.getElementById('progress-text').textContent = `Question ${currentQuestionIndex + 1} of ${questions.length}`;
    
    const container = document.getElementById('question-content');
    
    let optionsHtml = '';
    ['A', 'B', 'C', 'D'].forEach(opt => {
        const val = q[`option${opt}`];
        if (!val) return;
        
        const isSelected = answers[q.id] === val;
        optionsHtml += `
            <button 
                class="option-btn ${isSelected ? 'selected' : ''}" 
                onclick="selectOption(${q.id}, '${val.replace(/'/g, "\\'")}')"
            >
                <div style="display: flex; gap: 1rem; align-items: center;">
                    <div class="opt-letter ${isSelected ? 'active' : ''}">${opt}</div>
                    <span>${val}</span>
                </div>
            </button>
        `;
    });

    container.innerHTML = `
        <h3 style="font-size: 1.5rem; margin-bottom: 2rem; font-weight: 500; line-height: 1.5;">
            <span class="text-primary">Q${currentQuestionIndex + 1}.</span> ${q.content}
        </h3>
        <div style="display: flex; flex-direction: column; gap: 1rem;">
            ${optionsHtml}
        </div>
    `;

    document.getElementById('prev-btn').disabled = currentQuestionIndex === 0;
    
    const nextBtn = document.getElementById('next-btn');
    if (currentQuestionIndex === questions.length - 1) {
        nextBtn.innerHTML = 'Submit Exam';
        nextBtn.classList.remove('btn-outline');
        nextBtn.classList.add('btn-primary');
        nextBtn.onclick = submitExam;
    } else {
        nextBtn.innerHTML = 'Next';
        nextBtn.classList.remove('btn-primary');
        nextBtn.classList.add('btn-outline');
        nextBtn.onclick = nextQuestion;
    }
}

window.selectOption = (questionId, value) => {
    answers[questionId] = value;
    renderQuestion();
};

window.nextQuestion = () => {
    if (currentQuestionIndex < questions.length - 1) {
        currentQuestionIndex++;
        renderQuestion();
    }
};

window.prevQuestion = () => {
    if (currentQuestionIndex > 0) {
        currentQuestionIndex--;
        renderQuestion();
    }
};

window.submitExam = async () => {
    if (isSubmitting) return;
    isSubmitting = true;
    const btn = document.getElementById('next-btn');
    btn.innerHTML = 'Submitting...';
    btn.disabled = true;

    try {
        const payload = {
            attemptId: parseInt(attemptId),
            answers: answers
        };

        const result = await api.post(`/student/submit-exam/${attemptId}`, payload);
        window.location.href = `result.html?id=${result.id}`;
    } catch (error) {
        console.error('Submit error:', error);
        alert('Failed to submit exam. ' + error.message);
        isSubmitting = false;
        btn.innerHTML = 'Submit Exam';
        btn.disabled = false;
    }
};
