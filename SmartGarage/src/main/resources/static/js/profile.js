document.addEventListener("DOMContentLoaded", function() {
    // Edit Profile button handler
    const editButton = document.getElementById("edit-button");
    const editModal = new bootstrap.Modal(document.getElementById("editModal"));

    // Fetch user info from the DOM
    const userName = document.getElementById("user-name").innerText;
    const userEmail = document.getElementById("user-email").innerText;
    const userPhone = document.getElementById("user-phone").innerText;

    editButton.addEventListener("click", function() {
        // Populate the modal fields with the current user information
        document.getElementById("modal-username").value = userName;
        document.getElementById("modal-email").value = userEmail;
        document.getElementById("modal-phone").value = userPhone;

        // Show the edit modal
        editModal.show();
    });
});