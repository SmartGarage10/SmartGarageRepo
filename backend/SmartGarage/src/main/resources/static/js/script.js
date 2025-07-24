document.addEventListener("DOMContentLoaded", function() {
    // Handle edit button click
    document.querySelectorAll('button[data-bs-toggle="modal"]').forEach(button => {
        button.addEventListener('click', function() {
            // Get client data from data attributes
            const clientId = this.getAttribute('data-client-id');
            const username = this.getAttribute('data-username');
            const email = this.getAttribute('data-email');
            const phone = this.getAttribute('data-phone');

            // Populate modal fields
            const modal = document.getElementById('editModal');
            modal.querySelector('#modal-clientId').value = clientId;
            modal.querySelector('#modal-username').value = username;
            modal.querySelector('#modal-email').value = email;
            modal.querySelector('#modal-phone').value = phone;
        });
    });

    // Handle form submission for update
    document.getElementById('updateClientForm').addEventListener('submit', function(event) {
        event.preventDefault(); // Prevent the default form submission

        const formData = new FormData(this);
        const clientId = formData.get('clientId'); // Get clientId from formData

        fetch(`/employee/client/${clientId}/edit`, {
            method: 'POST',
            headers: {
                'X-Requested-With': 'XMLHttpRequest',
                'X-CSRF-TOKEN': document.querySelector('meta[name="csrf-token"]').getAttribute('content') // Get CSRF token from meta tag
            },
            body: formData
        })
            .then(response => {
                if (response.ok) {
                    return response.json(); // or response.text() if response is not JSON
                } else {
                    throw new Error('Network response was not ok.');
                }
            })
            .then(data => {
                // Handle the response data here
                console.log(data);

                // Close the modal
                const modal = bootstrap.Modal.getInstance(document.getElementById('editModal'));
                modal.hide();

            })
            .catch(error => {
                console.error('There has been a problem with your fetch operation:', error);
            });
    });

    // Handle delete button click
    document.querySelectorAll('button.btn-danger').forEach(button => {
        button.addEventListener('click', function() {
            const clientId = this.getAttribute('data-client-id');

            fetch(`/employee/client/${clientId}/delete`, {
                method: 'POST', // Use POST as your controller is configured for POST
                headers: {
                    'X-Requested-With': 'XMLHttpRequest',
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': document.querySelector('meta[name="csrf-token"]').getAttribute('content') // Get CSRF token from meta tag
                }
            })
                .then(response => {
                    if (response.ok) {
                        return response.text(); // Or handle JSON if response is JSON
                    } else {
                        throw new Error('Network response was not ok.');
                    }
                })
                .then(data => {
                    console.log(data);
                    location.reload(); // Refresh the client list
                })
                .catch(error => {
                    console.error('There has been a problem with your fetch operation:', error);
                });
        });
    });
});
