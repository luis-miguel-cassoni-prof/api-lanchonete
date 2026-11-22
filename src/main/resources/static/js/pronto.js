const readyName = document.querySelector("#readyName");
const readyMessage = document.querySelector("#readyMessage");

async function refreshReadyOrder() {
  try {
    const response = await fetch("/pedido/pronto", { credentials: "same-origin" });
    if (response.status === 204) {
      readyName.textContent = "Aguardando pedidos";
      readyMessage.textContent = "Quando um pedido ficar pronto, o nome aparecerá aqui.";
      return;
    }
    if (!response.ok) {
      throw new Error("Não foi possível consultar os pedidos.");
    }
    const readyOrder = await response.json();
    readyName.textContent = readyOrder.nomeCliente;
    readyMessage.textContent = "Seu pedido está pronto para retirada.";
  } catch (error) {
    readyMessage.textContent = error.message;
  }
}

refreshReadyOrder();
window.setInterval(refreshReadyOrder, 3000);
