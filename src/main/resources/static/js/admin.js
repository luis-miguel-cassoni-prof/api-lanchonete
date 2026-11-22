const loginView = document.querySelector("#loginView");
const adminView = document.querySelector("#adminView");
const loginForm = document.querySelector("#loginForm");
const loginMessage = document.querySelector("#loginMessage");
const adminMessage = document.querySelector("#adminMessage");
const menuForm = document.querySelector("#menuForm");
const orderForm = document.querySelector("#adminOrderForm");
const csrfState = { headerName: "X-CSRF-TOKEN", token: "" };
const state = { menu: [], orders: [], activeTab: "orders" };

const money = new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" });
const statusNames = {
  AGUARDANDO: "Aguardando",
  EM_PREPARO: "Em preparo",
  PRONTO: "Pronto",
  ENTREGUE: "Entregue"
};
const nextStatus = {
  AGUARDANDO: ["EM_PREPARO", "Iniciar preparo"],
  EM_PREPARO: ["PRONTO", "Marcar pronto"],
  PRONTO: ["ENTREGUE", "Marcar entregue"]
};

function escapeHtml(value) {
  return String(value ?? "").replace(/[&<>"']/g, character => ({
    "&": "&amp;",
    "<": "&lt;",
    ">": "&gt;",
    '"': "&quot;",
    "'": "&#39;"
  })[character]);
}

function showMessage(element, message, type = "") {
  element.textContent = message;
  element.classList.toggle("error", type === "error");
  element.classList.toggle("success", type === "success");
}

async function readResponse(response) {
  if (response.status === 204) return null;
  const text = await response.text();
  if (!text) return null;
  try {
    return JSON.parse(text);
  } catch {
    return text;
  }
}

async function api(path, options = {}) {
  const method = (options.method || "GET").toUpperCase();
  const headers = new Headers(options.headers || {});
  if (options.body && !headers.has("Content-Type")) {
    headers.set("Content-Type", "application/json");
  }
  if (!["GET", "HEAD", "OPTIONS"].includes(method) && csrfState.token) {
    headers.set(csrfState.headerName, csrfState.token);
  }

  const response = await fetch(path, {
    ...options,
    method,
    headers,
    credentials: "same-origin"
  });
  const body = await readResponse(response);
  if (!response.ok) {
    const message = body?.detail || body?.message || `A requisição falhou (${response.status}).`;
    const error = new Error(message);
    error.status = response.status;
    throw error;
  }
  return body;
}

async function loadCsrfToken() {
  const token = await api("/usuario/csrf");
  csrfState.headerName = token.headerName;
  csrfState.token = token.token;
}

function showLogin(message = "") {
  adminView.classList.add("hidden");
  loginView.classList.remove("hidden");
  showMessage(loginMessage, message, message ? "error" : "");
}

function showAdmin() {
  loginView.classList.add("hidden");
  adminView.classList.remove("hidden");
}

function setTab(tab) {
  state.activeTab = tab;
  const ordersActive = tab === "orders";
  document.querySelector("#ordersSection").classList.toggle("hidden", !ordersActive);
  document.querySelector("#menuSection").classList.toggle("hidden", ordersActive);
  document.querySelector("#ordersTab").classList.toggle("active", ordersActive);
  document.querySelector("#menuTab").classList.toggle("active", !ordersActive);
  document.querySelector("#ordersTab").setAttribute("aria-selected", String(ordersActive));
  document.querySelector("#menuTab").setAttribute("aria-selected", String(!ordersActive));
}

function renderMenu() {
  const count = document.querySelector("#menuAdminCount");
  const list = document.querySelector("#adminMenuList");
  const select = document.querySelector("#adminMenuSelect");
  count.textContent = `${state.menu.length} ${state.menu.length === 1 ? "lanche" : "lanches"}`;

  if (!state.menu.length) {
    list.innerHTML = '<p class="empty-state">Cadastre um lanche para começar.</p>';
  } else {
    list.innerHTML = state.menu.map(item => `
      <article class="admin-item">
        <div class="admin-item-header">
          <div>
            <h4>${escapeHtml(item.nomeLanche)}</h4>
            <p>${escapeHtml(item.descricao)}</p>
          </div>
          <strong class="price">${money.format(Number(item.valor))}</strong>
        </div>
        <div class="admin-item-actions">
          <button class="button button-outline button-small" type="button" data-edit-menu="${item.id}">Editar</button>
          <button class="button button-outline button-danger button-small" type="button" data-delete-menu="${item.id}">Excluir</button>
        </div>
      </article>
    `).join("");
  }

  select.innerHTML = state.menu.length
    ? state.menu.map(item => `<option value="${item.id}">${escapeHtml(item.nomeLanche)} · ${money.format(Number(item.valor))}</option>`).join("")
    : '<option value="">Cadastre um lanche antes</option>';
}

function renderOrders() {
  const queue = document.querySelector("#queueList");
  const count = document.querySelector("#queueCount");
  const ordered = [...state.orders].sort((a, b) => a.id - b.id);
  const nextOrder = ordered.find(order => order.status !== "ENTREGUE");

  count.textContent = `${ordered.length} ${ordered.length === 1 ? "pedido" : "pedidos"}`;
  if (!ordered.length) {
    queue.innerHTML = '<p class="empty-state">Ainda não chegaram pedidos.</p>';
    return;
  }

  queue.innerHTML = ordered.map(order => {
    const isNext = nextOrder?.id === order.id;
    const action = nextStatus[order.status];
    return `
      <article class="queue-item${isNext ? " is-next" : ""}">
        <div class="queue-item-header">
          <div>
            <h4>#${order.id} · ${escapeHtml(order.nomeCliente)}</h4>
            <p>${escapeHtml(order.lanche?.nomeLanche || "Lanche removido")} · ${order.quantidade} un. · ${money.format(Number(order.totalPedido))}</p>
          </div>
          <span class="status-pill status-${escapeHtml(order.status)}">${escapeHtml(statusNames[order.status] || order.status)}</span>
        </div>
        <div class="queue-actions">
          ${action ? `<button class="button button-primary button-small" type="button" data-advance-order="${order.id}" data-next-status="${action[0]}" ${isNext ? "" : "disabled"}>${isNext ? action[1] : "Aguardando anterior"}</button>` : ""}
          <button class="button button-outline button-small" type="button" data-edit-order="${order.id}">Editar</button>
          <button class="button button-outline button-danger button-small" type="button" data-delete-order="${order.id}">Excluir</button>
        </div>
      </article>
    `;
  }).join("");
}

async function loadDashboard() {
  try {
    const [menu, orders] = await Promise.all([
      api("/cardapio/listarTudo"),
      api("/pedido/listarTudo")
    ]);
    state.menu = menu;
    state.orders = orders;
    renderMenu();
    renderOrders();
    showAdmin();
    return true;
  } catch (error) {
    if (error.status === 401) {
      showLogin("Sua sessão expirou. Entre novamente.");
      return false;
    }
    if (error.status === 403) {
      showLogin("Esta conta não possui permissão de administrador.");
      return false;
    }
    showMessage(adminMessage, error.message, "error");
    return false;
  }
}

function resetMenuForm() {
  menuForm.reset();
  document.querySelector("#menuEditId").value = "";
  document.querySelector("#menuFormTitle").textContent = "Adicionar lanche";
  document.querySelector("#saveMenu").textContent = "Adicionar lanche";
  document.querySelector("#cancelMenuEdit").classList.add("hidden");
}

function resetOrderForm() {
  orderForm.reset();
  document.querySelector("#orderEditId").value = "";
  document.querySelector("#adminQuantity").value = "1";
  document.querySelector("#orderFormTitle").textContent = "Registrar pedido";
  document.querySelector("#saveOrder").textContent = "Adicionar pedido";
  document.querySelector("#cancelOrderEdit").classList.add("hidden");
}

async function submitLogin(event) {
  event.preventDefault();
  showMessage(loginMessage, "");
  const formData = new FormData(loginForm);
  const button = loginForm.querySelector('button[type="submit"]');
  button.disabled = true;
  button.textContent = "Entrando…";
  try {
    await loadCsrfToken();
    await api("/usuario/login", {
      method: "POST",
      body: JSON.stringify({
        nome: String(formData.get("nome")).trim(),
        senha: String(formData.get("senha"))
      })
    });
    await loadCsrfToken();
    if (await loadDashboard()) {
      loginForm.reset();
      showMessage(adminMessage, "Sessão administrativa iniciada.", "success");
    }
  } catch (error) {
    showMessage(loginMessage, error.status === 401 ? "Usuário ou senha inválidos." : error.message, "error");
  } finally {
    button.disabled = false;
    button.textContent = "Entrar";
  }
}

async function submitMenu(event) {
  event.preventDefault();
  const editId = document.querySelector("#menuEditId").value;
  const formData = new FormData(menuForm);
  const payload = {
    nomeLanche: String(formData.get("nomeLanche")).trim(),
    descricao: String(formData.get("descricao")).trim(),
    valor: Number(formData.get("valor"))
  };
  try {
    await api(editId ? `/cardapio/${editId}` : "/cardapio", {
      method: editId ? "PUT" : "POST",
      body: JSON.stringify(payload)
    });
    resetMenuForm();
    await loadDashboard();
    showMessage(adminMessage, editId ? "Lanche atualizado." : "Lanche cadastrado.", "success");
  } catch (error) {
    showMessage(adminMessage, error.message, "error");
  }
}

async function submitOrder(event) {
  event.preventDefault();
  const editId = document.querySelector("#orderEditId").value;
  const formData = new FormData(orderForm);
  const payload = {
    nomeCliente: String(formData.get("nomeCliente")).trim(),
    lancheId: Number(formData.get("lancheId")),
    quantidade: Number(formData.get("quantidade"))
  };
  try {
    await api(editId ? `/pedido/${editId}` : "/pedido", {
      method: editId ? "PUT" : "POST",
      body: JSON.stringify(payload)
    });
    resetOrderForm();
    await loadDashboard();
    showMessage(adminMessage, editId ? "Pedido atualizado." : "Pedido adicionado à fila.", "success");
  } catch (error) {
    showMessage(adminMessage, error.message, "error");
  }
}

function editMenu(id) {
  const item = state.menu.find(entry => entry.id === Number(id));
  if (!item) return;
  document.querySelector("#menuEditId").value = item.id;
  document.querySelector("#menuName").value = item.nomeLanche;
  document.querySelector("#menuDescription").value = item.descricao;
  document.querySelector("#menuPrice").value = Number(item.valor).toFixed(2);
  document.querySelector("#menuFormTitle").textContent = "Editar lanche";
  document.querySelector("#saveMenu").textContent = "Salvar alterações";
  document.querySelector("#cancelMenuEdit").classList.remove("hidden");
  setTab("menu");
  document.querySelector("#menuName").focus();
}

function editOrder(id) {
  const order = state.orders.find(entry => entry.id === Number(id));
  if (!order) return;
  document.querySelector("#orderEditId").value = order.id;
  document.querySelector("#adminCustomerName").value = order.nomeCliente;
  document.querySelector("#adminMenuSelect").value = String(order.lanche?.id || "");
  document.querySelector("#adminQuantity").value = order.quantidade;
  document.querySelector("#orderFormTitle").textContent = `Editar pedido #${order.id}`;
  document.querySelector("#saveOrder").textContent = "Salvar alterações";
  document.querySelector("#cancelOrderEdit").classList.remove("hidden");
  setTab("orders");
  document.querySelector("#adminCustomerName").focus();
}

async function handleAdminListClick(event) {
  const editButton = event.target.closest("[data-edit-menu]");
  const deleteButton = event.target.closest("[data-delete-menu]");
  if (editButton) {
    editMenu(editButton.dataset.editMenu);
    return;
  }
  if (deleteButton) {
    const id = deleteButton.dataset.deleteMenu;
    if (!window.confirm("Excluir este lanche do cardápio?")) return;
    try {
      await api(`/cardapio/${id}`, { method: "DELETE" });
      await loadDashboard();
      showMessage(adminMessage, "Lanche excluído.", "success");
    } catch (error) {
      showMessage(adminMessage, error.message, "error");
    }
  }
}

async function handleQueueClick(event) {
  const advanceButton = event.target.closest("[data-advance-order]");
  const editButton = event.target.closest("[data-edit-order]");
  const deleteButton = event.target.closest("[data-delete-order]");
  if (advanceButton) {
    try {
      await api(`/pedido/${advanceButton.dataset.advanceOrder}/status`, {
        method: "PATCH",
        body: JSON.stringify({ status: advanceButton.dataset.nextStatus })
      });
      await loadDashboard();
      showMessage(adminMessage, "Status do pedido atualizado.", "success");
    } catch (error) {
      showMessage(adminMessage, error.message, "error");
    }
    return;
  }
  if (editButton) {
    editOrder(editButton.dataset.editOrder);
    return;
  }
  if (deleteButton) {
    const id = deleteButton.dataset.deleteOrder;
    if (!window.confirm(`Excluir o pedido #${id}?`)) return;
    try {
      await api(`/pedido/${id}`, { method: "DELETE" });
      await loadDashboard();
      showMessage(adminMessage, "Pedido excluído.", "success");
    } catch (error) {
      showMessage(adminMessage, error.message, "error");
    }
  }
}

async function logout() {
  try {
    await loadCsrfToken();
    await api("/usuario/logout", { method: "POST" });
    csrfState.token = "";
    showLogin("Sessão encerrada.");
  } catch (error) {
    showMessage(adminMessage, error.message, "error");
  }
}

loginForm.addEventListener("submit", submitLogin);
menuForm.addEventListener("submit", submitMenu);
orderForm.addEventListener("submit", submitOrder);
document.querySelector("#adminMenuList").addEventListener("click", handleAdminListClick);
document.querySelector("#queueList").addEventListener("click", handleQueueClick);
document.querySelector("#logoutButton").addEventListener("click", logout);
document.querySelector("#refreshOrders").addEventListener("click", loadDashboard);
document.querySelector("#ordersTab").addEventListener("click", () => setTab("orders"));
document.querySelector("#menuTab").addEventListener("click", () => setTab("menu"));
document.querySelector("#cancelMenuEdit").addEventListener("click", resetMenuForm);
document.querySelector("#cancelOrderEdit").addEventListener("click", resetOrderForm);

loadCsrfToken()
  .then(loadDashboard)
  .catch(error => showMessage(loginMessage, error.message, "error"));

window.setInterval(() => {
  if (!adminView.classList.contains("hidden")) {
    loadDashboard();
  }
}, 7000);
