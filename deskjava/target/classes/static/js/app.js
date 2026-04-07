// Global State
let currentUser = JSON.parse(localStorage.getItem('user')) || null;
let authHeader = localStorage.getItem('auth') || null;

// UI Elements
const loginSection = document.getElementById('login-section');
const registerForm = document.getElementById('register-form');
const loginForm = document.getElementById('login-form');
const dashboardSection = document.getElementById('dashboard-section');
const userDisplayName = document.getElementById('user-display-name');
const ticketsList = document.getElementById('tickets-list');
const newTicketContainer = document.getElementById('new-ticket-container');
const ticketListContainer = document.getElementById('ticket-list-container');
const newTicketForm = document.getElementById('new-ticket-form');

// New Management UI Elements
const navClients = document.getElementById('nav-clients');
const navTechnicians = document.getElementById('nav-technicians');
const sectionTickets = document.getElementById('section-tickets');
const sectionClients = document.getElementById('section-clients');
const sectionTechnicians = document.getElementById('section-technicians');
const sectionProfile = document.getElementById('section-profile');
const clientsTableBody = document.getElementById('clients-table-body');
const techniciansTableBody = document.getElementById('technicians-table-body');
const techModal = document.getElementById('tech-modal');
const newTechForm = document.getElementById('new-tech-form');
const profileForm = document.getElementById('profile-form');
const forgotForm = document.getElementById('forgot-form');

// Detail & Chat Elements
const ticketDetailContainer = document.getElementById('ticket-detail-container');
const detailTicketTitle = document.getElementById('detail-ticket-title');
const detailTicketStatus = document.getElementById('detail-ticket-status');
const detailTicketPriority = document.getElementById('detail-ticket-priority');
const detailTicketDesc = document.getElementById('detail-ticket-desc');
const detailTicketTech = document.getElementById('detail-ticket-tech');
const chatMessages = document.getElementById('chat-messages');
const chatForm = document.getElementById('chat-form');
const chatInput = document.getElementById('chat-input');
const btnCloseTicket = document.getElementById('btn-close-ticket');
const btnAssumirTicket = document.getElementById('btn-assumir-ticket');
const btnTransferTicket = document.getElementById('btn-transfer-ticket');
const transferModal = document.getElementById('transfer-modal');
const transferForm = document.getElementById('transfer-form');
const transferTechSelect = document.getElementById('transfer-tech');

let currentTicketId = null;

// Initialize
document.addEventListener('DOMContentLoaded', () => {
    if (currentUser && authHeader) {
        showDashboard();
    } else {
        showLogin();
    }
});

// Auth Functions
function showAuthTab(tab) {
    loginForm.classList.add('hidden');
    registerForm.classList.add('hidden');
    forgotForm.classList.add('hidden');
    
    if (tab === 'login') {
        loginForm.classList.remove('hidden');
        document.getElementById('tab-login').classList.add('active');
        document.getElementById('tab-register').classList.remove('active');
    } else if (tab === 'register') {
        registerForm.classList.remove('hidden');
        document.getElementById('tab-login').classList.remove('active');
        document.getElementById('tab-register').classList.add('active');
    } else if (tab === 'forgot') {
        forgotForm.classList.remove('hidden');
    }
}

loginForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const email = document.getElementById('login-email').value;
    const senha = document.getElementById('login-password').value;

    try {
        const response = await fetch('/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, senha })
        });

        if (response.ok) {
            const data = await response.json();
            currentUser = data;
            authHeader = 'Basic ' + btoa(email + ':' + senha);
            localStorage.setItem('user', JSON.stringify(currentUser));
            localStorage.setItem('auth', authHeader);
            showDashboard();
        } else {
            alert('Falha no login. Verifique suas credenciais.');
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Erro ao conectar com o servidor.');
    }
});

registerForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const nome = document.getElementById('reg-name').value;
    const cpf = document.getElementById('reg-cpf').value;
    const telefone = document.getElementById('reg-phone').value;
    const email = document.getElementById('reg-email').value;
    const senha = document.getElementById('reg-password').value;

    try {
        const response = await fetch('/clientes/criar', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ nome, cpf, telefone, email, senha })
        });

        if (response.ok) {
            alert('Conta criada com sucesso! Faça login.');
            showAuthTab('login');
        } else {
            alert('Erro ao criar conta.');
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Erro ao conectar com o servidor.');
    }
});

document.getElementById('logout-btn').onclick = () => {
    localStorage.clear();
    location.reload();
};

// Dashboard Functions
function showDashboard() {
    loginSection.classList.add('hidden');
    dashboardSection.classList.remove('hidden');
    userDisplayName.textContent = currentUser.nome || currentUser.email;

    // Admin/Technician conditional navigation
    if (currentUser.perfil === 'TECNICO') {
        navClients.classList.remove('hidden');
        navTechnicians.classList.remove('hidden');
    } else {
        navClients.classList.add('hidden');
        navTechnicians.classList.add('hidden');
    }

    showSection('tickets');
}

function showSection(section) {
    // Hide all sections
    sectionTickets.classList.add('hidden');
    sectionClients.classList.add('hidden');
    sectionTechnicians.classList.add('hidden');
    sectionProfile.classList.add('hidden');

    // Remove active class from nav
    document.querySelectorAll('.sidebar-nav li').forEach(li => li.classList.remove('active'));

    // Show selected section
    if (section === 'tickets') {
        sectionTickets.classList.remove('hidden');
        document.getElementById('nav-tickets').classList.add('active');
        loadTickets();
    } else if (section === 'clients') {
        sectionClients.classList.remove('hidden');
        document.getElementById('nav-clients').classList.add('active');
        loadClients();
    } else if (section === 'technicians') {
        sectionTechnicians.classList.remove('hidden');
        document.getElementById('nav-technicians').classList.add('active');
        loadTechnicians();
    } else if (section === 'profile') {
        sectionProfile.classList.remove('hidden');
        document.getElementById('nav-profile').classList.add('active');
        loadProfile();
    }
}

function loadProfile() {
    document.getElementById('profile-name').value = currentUser.nome;
    document.getElementById('profile-email').value = currentUser.email;
}

profileForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const nome = document.getElementById('profile-name').value;
    const senha = document.getElementById('profile-password').value;

    const data = { nome };
    if (senha) data.senha = senha;

    try {
        const endpoint = currentUser.perfil === 'TECNICO' 
            ? `/tecnicos/perfil/${currentUser.id}`
            : `/clientes/atualizar/${currentUser.id}`; // Nota: Backend de cliente precisa suportar DTO similar

        const response = await fetch(endpoint, {
            method: 'PUT',
            headers: { 
                'Content-Type': 'application/json',
                'Authorization': authHeader
            },
            body: JSON.stringify(data)
        });

        if (response.ok) {
            const updated = await response.json();
            currentUser.nome = updated.nome;
            localStorage.setItem('user', JSON.stringify(currentUser));
            if (senha) {
                // Se a senha mudou, precisa atualizar o Basic Auth header
                authHeader = 'Basic ' + btoa(currentUser.email + ':' + senha);
                localStorage.setItem('auth', authHeader);
            }
            userDisplayName.textContent = currentUser.nome;
            alert('Perfil atualizado com sucesso!');
            document.getElementById('profile-password').value = '';
        } else {
            alert('Erro ao atualizar perfil.');
        }
    } catch (error) {
        console.error('Error:', error);
    }
});

forgotForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const email = document.getElementById('forgot-email').value;
    // Simulação de envio de e-mail (funcionalidade backend não implementada totalmente)
    alert(`Se o e-mail ${email} estiver cadastrado, você receberá instruções de recuperação em breve.`);
    showAuthTab('login');
});

async function loadClients() {
    try {
        const response = await fetch('/clientes/listar', {
            headers: { 'Authorization': authHeader }
        });
        if (response.ok) {
            const data = await response.json();
            renderClients(data.content || []);
        }
    } catch (error) {
        console.error('Error loading clients:', error);
    }
}

function renderClients(clients) {
    clientsTableBody.innerHTML = '';
    clients.forEach(client => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${client.id}</td>
            <td>${client.nome}</td>
            <td>${client.email}</td>
            <td>${client.cpf}</td>
            <td>
                <button class="btn-outline btn-sm" onclick="deleteClient(${client.id})">Deletar</button>
            </td>
        `;
        clientsTableBody.appendChild(row);
    });
}

async function loadTechnicians() {
    try {
        const response = await fetch('/tecnicos/listar/completo', {
            headers: { 'Authorization': authHeader }
        });
        if (response.ok) {
            const data = await response.json();
            renderTechnicians(data.content || []);
        }
    } catch (error) {
        console.error('Error loading technicians:', error);
    }
}

function renderTechnicians(technicians) {
    techniciansTableBody.innerHTML = '';
    technicians.forEach(tech => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${tech.id}</td>
            <td>${tech.nome}</td>
            <td>${tech.email}</td>
            <td><span class="badge">${tech.nivel}</span></td>
            <td><span class="badge ${tech.ativo ? 'badge-active' : 'badge-inactive'}">${tech.ativo ? 'Ativo' : 'Suspenso'}</span></td>
            <td>
                <button class="btn-outline btn-sm" onclick="toggleTechStatus(${tech.id})">${tech.ativo ? 'Suspender' : 'Reativar'}</button>
            </td>
        `;
        techniciansTableBody.appendChild(row);
    });
}

async function deleteClient(id) {
    if (confirm('Tem certeza que deseja deletar este cliente?')) {
        try {
            const response = await fetch(`/clientes/deletar/${id}`, {
                method: 'DELETE',
                headers: { 'Authorization': authHeader }
            });
            if (response.ok) {
                alert('Cliente deletado com sucesso!');
                loadClients();
            }
        } catch (error) {
            console.error('Error deleting client:', error);
        }
    }
}

async function toggleTechStatus(id) {
    try {
        const response = await fetch(`/tecnicos/suspender/${id}`, {
            method: 'PUT',
            headers: { 'Authorization': authHeader }
        });
        if (response.ok) {
            alert('Status do técnico atualizado!');
            loadTechnicians();
        }
    } catch (error) {
        console.error('Error toggling tech status:', error);
    }
}

function showNewTechnicianModal() {
    techModal.classList.remove('hidden');
}

function hideTechModal() {
    techModal.classList.add('hidden');
    newTechForm.reset();
}

newTechForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const nome = document.getElementById('tech-name').value;
    const email = document.getElementById('tech-email').value;
    const senha = document.getElementById('tech-password').value;
    const nivel = document.getElementById('tech-level').value;

    try {
        const response = await fetch('/tecnicos/criar', {
            method: 'POST',
            headers: { 
                'Content-Type': 'application/json',
                'Authorization': authHeader
            },
            body: JSON.stringify({ nome, email, senha, nivel })
        });

        if (response.ok) {
            alert('Técnico criado com sucesso!');
            hideTechModal();
            loadTechnicians();
        } else {
            alert('Erro ao criar técnico.');
        }
    } catch (error) {
        console.error('Error creating technician:', error);
    }
});

function showLogin() {
    loginSection.classList.remove('hidden');
    dashboardSection.classList.add('hidden');
}

async function loadTickets() {
    try {
        const statusFilter = document.getElementById('filter-status').value;
        let endpoint = currentUser.perfil === 'CLIENTE' 
            ? `/tickets/listar/cliente/${currentUser.id}`
            : `/tickets/listar/todos`; // Alterado para buscar todos para técnicos

        // Add status filter if selected for technicians
        if (statusFilter && currentUser.perfil === 'TECNICO') {
            endpoint = `/tickets/listar/status/${statusFilter}/${currentUser.id}`;
        }

        const response = await fetch(endpoint, {
            headers: { 'Authorization': authHeader }
        });

        if (response.ok) {
            const data = await response.json();
            renderTickets(data.content || []);
        }
    } catch (error) {
        console.error('Error:', error);
    }
}

function renderTickets(tickets) {
    ticketsList.innerHTML = '';
    if (tickets.length === 0) {
        ticketsList.innerHTML = '<p>Nenhum ticket encontrado.</p>';
        return;
    }

    tickets.forEach(ticket => {
        const card = document.createElement('div');
        card.className = 'ticket-card';
        card.onclick = () => showTicketDetail(ticket);
        card.style.cursor = 'pointer';
        card.innerHTML = `
            <div class="ticket-header">
                <span class="ticket-id">#${ticket.id}</span>
                <span class="status-badge status-${ticket.status}">${ticket.status}</span>
            </div>
            <h3>${ticket.titulo}</h3>
            <p>${ticket.descricao.substring(0, 100)}${ticket.descricao.length > 100 ? '...' : ''}</p>
            <div class="ticket-footer">
                <small>Prioridade: ${ticket.prioridade || 'Normal'}</small>
            </div>
        `;
        ticketsList.appendChild(card);
    });
}

async function showTicketDetail(ticket) {
    currentTicketId = ticket.id;
    sectionTickets.classList.remove('hidden');
    ticketListContainer.classList.add('hidden');
    ticketDetailContainer.classList.remove('hidden');
    newTicketContainer.classList.add('hidden');

    detailTicketTitle.textContent = `#${ticket.id} - ${ticket.titulo}`;
    detailTicketStatus.textContent = ticket.status;
    detailTicketStatus.className = `status-badge status-${ticket.status}`;
    detailTicketPriority.textContent = ticket.prioridade;
    detailTicketDesc.textContent = ticket.descricao;
    detailTicketTech.textContent = ticket.tecnicoNome || 'Não atribuído';

    // Control visibility of priority edit, assume, transfer and close button
    const priorityEdit = document.getElementById('tech-priority-edit');
    if (currentUser.perfil === 'TECNICO') {
        priorityEdit.classList.remove('hidden');
        document.getElementById('select-priority').value = ticket.prioridade;
        
        // Show "Assumir" if no technician is assigned
        if (ticket.tecnicoNome === 'Não atribuído' || ticket.tecnicoNome === null) {
            btnAssumirTicket.classList.remove('hidden');
            btnTransferTicket.classList.add('hidden');
        } else {
            btnAssumirTicket.classList.add('hidden');
            btnTransferTicket.classList.remove('hidden');
        }

        if (ticket.status !== 'FECHADO' && ticket.status !== 'CONCLUIDO') {
            btnCloseTicket.classList.remove('hidden');
        } else {
            btnCloseTicket.classList.add('hidden');
        }
    } else {
        priorityEdit.classList.add('hidden');
        btnAssumirTicket.classList.add('hidden');
        btnTransferTicket.classList.add('hidden');
        btnCloseTicket.classList.add('hidden');
    }

    // Iniciar polling de mensagens
    loadMessages();
    if (window.chatInterval) clearInterval(window.chatInterval);
    window.chatInterval = setInterval(loadMessages, 3000); // Atualiza a cada 3 segundos
}

async function updatePriority() {
    const priority = document.getElementById('select-priority').value;
    try {
        const response = await fetch(`/tickets/${currentTicketId}/prioridade/${priority}`, {
            method: 'PUT',
            headers: { 'Authorization': authHeader }
        });
        if (response.ok) {
            const updatedTicket = await response.json();
            detailTicketPriority.textContent = updatedTicket.prioridade;
            alert('Prioridade atualizada!');
            loadTickets(); // Refresh the list in background
        }
    } catch (error) {
        console.error('Error updating priority:', error);
    }
}

async function assumirTicket() {
    if (!currentTicketId) return;
    try {
        const response = await fetch(`/tickets/${currentTicketId}/assumir/${currentUser.id}`, {
            method: 'PUT',
            headers: { 'Authorization': authHeader }
        });
        if (response.ok) {
            const updatedTicket = await response.json();
            alert('Você assumiu este ticket!');
            detailTicketTech.textContent = updatedTicket.tecnicoNome;
            btnAssumirTicket.classList.add('hidden');
            btnTransferTicket.classList.remove('hidden');
            detailTicketStatus.textContent = updatedTicket.status;
            detailTicketStatus.className = `status-badge status-${updatedTicket.status}`;
            loadTickets();
        }
    } catch (error) {
        console.error('Error assuming ticket:', error);
    }
}

async function showTransferModal() {
    transferModal.classList.remove('hidden');
    // Carregar técnicos para o select
    try {
        const response = await fetch('/tecnicos/listar/completo', {
            headers: { 'Authorization': authHeader }
        });
        if (response.ok) {
            const data = await response.json();
            const technicians = data.content || [];
            transferTechSelect.innerHTML = '<option value="">Nenhum (Tornar Disponível)</option>';
            technicians.forEach(tech => {
                // Não listar o técnico atual se ele já for o responsável
                if (tech.nome !== detailTicketTech.textContent) {
                    const option = document.createElement('option');
                    option.value = tech.id;
                    option.textContent = `${tech.nome} (${tech.nivel})`;
                    transferTechSelect.appendChild(option);
                }
            });
        }
    } catch (error) {
        console.error('Error loading technicians for transfer:', error);
    }
}

function hideTransferModal() {
    transferModal.classList.add('hidden');
    transferForm.reset();
}

transferForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const novoTecnicoId = document.getElementById('transfer-tech').value;
    const novoNivel = document.getElementById('transfer-level').value;

    let url = `/tickets/${currentTicketId}/transferir?`;
    if (novoTecnicoId) url += `novoTecnicoId=${novoTecnicoId}&`;
    if (novoNivel) url += `novoNivel=${novoNivel}`;

    try {
        const response = await fetch(url, {
            method: 'PUT',
            headers: { 'Authorization': authHeader }
        });

        if (response.ok) {
            const updatedTicket = await response.json();
            alert('Ticket transferido com sucesso!');
            hideTransferModal();
            // Atualizar UI de detalhes
            detailTicketTech.textContent = updatedTicket.tecnicoNome;
            detailTicketStatus.textContent = updatedTicket.status;
            detailTicketStatus.className = `status-badge status-${updatedTicket.status}`;
            detailTicketPriority.textContent = updatedTicket.prioridade;
            
            // Re-checar botões
            if (updatedTicket.tecnicoNome === 'Não atribuído') {
                btnAssumirTicket.classList.remove('hidden');
                btnTransferTicket.classList.add('hidden');
            } else {
                btnAssumirTicket.classList.add('hidden');
                btnTransferTicket.classList.remove('hidden');
            }
            
            loadTickets();
        } else {
            alert('Erro ao transferir ticket.');
        }
    } catch (error) {
        console.error('Error transferring ticket:', error);
    }
});

async function loadMessages() {
    if (!currentTicketId) return;
    try {
        const response = await fetch(`/tickets/${currentTicketId}/mensagens`, {
            headers: { 'Authorization': authHeader }
        });
        if (response.ok) {
            const messages = await response.json();
            renderMessages(messages);
        }
    } catch (error) {
        console.error('Error loading messages:', error);
    }
}

function renderMessages(messages) {
    chatMessages.innerHTML = '';
    messages.forEach(msg => {
        const div = document.createElement('div');
        const isMe = msg.remetenteNome === currentUser.nome;
        
        // Identify if sender is a CLIENT
        const isClient = (msg.remetenteNome && msg.remetenteNome.toLowerCase().includes('cliente')) || 
                         (currentUser.perfil === 'CLIENTE' && isMe) ||
                         (currentUser.perfil === 'TECNICO' && !isMe);

        div.className = `message-item ${isMe ? 'message-sent' : 'message-received'}`;
        div.innerHTML = `
            <span class="message-sender">${msg.remetenteNome || 'Usuário'} ${isClient ? '<small class="badge badge-inactive" style="font-size: 0.6rem; padding: 1px 4px;">CLIENTE</small>' : ''}</span>
            <p>${msg.conteudo}</p>
        `;
        chatMessages.appendChild(div);
    });
    chatMessages.scrollTop = chatMessages.scrollHeight;
}

chatForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const conteudo = chatInput.value;
    try {
        const response = await fetch(`/tickets/${currentTicketId}/mensagens`, {
            method: 'POST',
            headers: { 
                'Content-Type': 'application/json',
                'Authorization': authHeader
            },
            body: JSON.stringify({ conteudo })
        });
        if (response.ok) {
            chatInput.value = '';
            loadMessages();
        }
    } catch (error) {
        console.error('Error sending message:', error);
    }
});

async function closeTicket() {
    if (!currentTicketId) return;
    if (confirm('Deseja realmente fechar este ticket?')) {
        try {
            const response = await fetch(`/tickets/fechar/${currentTicketId}`, {
                method: 'PUT',
                headers: { 'Authorization': authHeader }
            });
            if (response.ok) {
                alert('Ticket fechado com sucesso!');
                showTicketList();
                loadTickets();
            }
        } catch (error) {
            console.error('Error closing ticket:', error);
        }
    }
}

// Ticket Management
document.getElementById('btn-new-ticket').onclick = () => {
    newTicketContainer.classList.remove('hidden');
    ticketListContainer.classList.add('hidden');
};

function showTicketList() {
    newTicketContainer.classList.add('hidden');
    ticketDetailContainer.classList.add('hidden');
    ticketListContainer.classList.remove('hidden');
    currentTicketId = null;
    
    // Parar polling de mensagens
    if (window.chatInterval) {
        clearInterval(window.chatInterval);
        window.chatInterval = null;
    }
}

newTicketForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const titulo = document.getElementById('ticket-title').value;
    const descricao = document.getElementById('ticket-desc').value;

    const ticketData = {
        clienteId: currentUser.id,
        titulo,
        descricao
    };

    try {
        const response = await fetch('/tickets/abrir', {
            method: 'POST',
            headers: { 
                'Content-Type': 'application/json',
                'Authorization': authHeader
            },
            body: JSON.stringify(ticketData)
        });

        if (response.ok) {
            alert('Ticket criado com sucesso!');
            newTicketForm.reset();
            showTicketList();
            loadTickets();
        } else {
            alert('Erro ao criar ticket.');
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Erro ao conectar com o servidor.');
    }
});
