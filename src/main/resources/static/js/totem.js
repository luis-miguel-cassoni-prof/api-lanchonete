const menuGrid = document.querySelector("#menuGrid");
const menuMessage = document.querySelector("#menuMessage");
const menuCount = document.querySelector("#menuCount");
const orderForm = document.querySelector("#orderForm");
const customerName = document.querySelector("#customerName");
const quantityInput = document.querySelector("#quantity");
const selectedItem = document.querySelector("#selectedItem");
const orderTotal = document.querySelector("#orderTotal");
const orderMessage = document.querySelector("#orderMessage");
const submitOrder = document.querySelector("#submitOrder");

const money = new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" });
let menuItems = [];
let selectedMenuItem = null;

function escapeHtml(value) {
  return String(value ?? "").replace(/[&<>"']/g, character => ({
    "&": "&amp;",
    "<": "&lt;",
    ">": "&gt;",
    '"': "&quot;",
    "'": "&#39;"
  })[character]);
}

function setMessage(element, message, type = "") {
  element.textContent = message;
  element.classList.toggle("error", type === "error");
  element.classList.toggle("success", type === "success");
}

function updateSummary() {
  const quantity = Math.max(1, Number(quantityInput.value) || 1);
  quantityInput.value = String(quantity);
  const total = selectedMenuItem ? Number(selectedMenuItem.valor) * quantity : 0;
  orderTotal.textContent = money.format(total);
  submitOrder.disabled = !selectedMenuItem;

  if (!selectedMenuItem) {
    selectedItem.innerHTML = '<span class="selected-icon" aria-hidden="true">+</span><span class="muted">Escolha um lanche do cardápio</span>';
    return;
  }

  selectedItem.innerHTML = `<span class="selected-icon" aria-hidden="true">✓</span><span><strong>${escapeHtml(selectedMenuItem.nomeLanche)}</strong><br><span class="muted">${money.format(Number(selectedMenuItem.valor))} cada</span></span>`;
}

function renderMenu() {
  menuCount.textContent = `${menuItems.length} ${menuItems.length === 1 ? "opção" : "opções"}`;
  if (!menuItems.length) {
    menuGrid.replaceChildren();
    setMessage(menuMessage, "Nenhum lanche está disponível no momento.", "error");
    return;
  }

  setMessage(menuMessage, "");
  menuGrid.innerHTML = menuItems.map(item => `
    <article class="menu-card${selectedMenuItem?.id === item.id ? " selected" : ""}">
      <div>
        <h3>${escapeHtml(item.nomeLanche)}</h3>
        <p>${escapeHtml(item.descricao || "")}</p>
      </div>
      <div class="menu-card-footer">
        <span class="price">${money.format(Number(item.valor))}</span>
        <button class="button button-primary button-small" type="button" data-select-menu="${escapeHtml(item.id)}">
          ${selectedMenuItem?.id === item.id ? "Selecionado" : "Escolher"}
        </button>
      </div>
    </article>
  `).join("");
}

async function loadMenu() {
  try {
    const response = await fetch("/cardapio/listarTudo", { credentials: "same-origin" });
    if (!response.ok) {
      throw new Error("Não foi possível carregar o cardápio.");
    }
    menuItems = await response.json();
    renderMenu();
  } catch (error) {
    setMessage(menuMessage, error.message, "error");
    menuCount.textContent = "Indisponível";
  }
}

menuGrid.addEventListener("click", event => {
  const button = event.target.closest("[data-select-menu]");
  if (!button) return;
  selectedMenuItem = menuItems.find(item => String(item.id) === button.dataset.selectMenu) || null;
  setMessage(orderMessage, "");
  renderMenu();
  updateSummary();
});

quantityInput.addEventListener("input", updateSummary);
document.querySelector("#decreaseQuantity").addEventListener("click", () => {
  quantityInput.value = String(Math.max(1, Number(quantityInput.value) - 1));
  updateSummary();
});
document.querySelector("#increaseQuantity").addEventListener("click", () => {
  quantityInput.value = String(Math.min(99, Number(quantityInput.value) + 1));
  updateSummary();
});

orderForm.addEventListener("submit", async event => {
  event.preventDefault();
  if (!selectedMenuItem) {
    setMessage(orderMessage, "Escolha um lanche antes de enviar o pedido.", "error");
    return;
  }

  submitOrder.disabled = true;
  submitOrder.textContent = "Enviando…";
  setMessage(orderMessage, "");

  try {
    const response = await fetch("/pedido", {
      method: "POST",
      credentials: "same-origin",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        nomeCliente: customerName.value.trim(),
        lancheId: Number(selectedMenuItem.id),
        quantidade: Number(quantityInput.value)
      })
    });

    const result = response.status === 204 ? null : await response.json();
    if (!response.ok) {
      throw new Error(result?.message || result?.detail || "Não foi possível enviar o pedido.");
    }

    setMessage(orderMessage, `Pedido #${result.id} enviado! O pagamento será feito no balcão.`, "success");
    orderForm.reset();
    quantityInput.value = "1";
    selectedMenuItem = null;
    renderMenu();
    updateSummary();
  } catch (error) {
    setMessage(orderMessage, error.message, "error");
  } finally {
    submitOrder.textContent = "Enviar pedido";
    submitOrder.disabled = !selectedMenuItem;
  }
});

loadMenu();
