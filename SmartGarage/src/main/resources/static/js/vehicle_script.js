document.addEventListener('DOMContentLoaded', function() {
    // Handle Edit Button Click
    document.querySelectorAll('.btn-primary[data-bs-toggle="modal"]').forEach(button => {
        button.addEventListener('click', function() {
            const vehicleId = this.getAttribute('data-vehicle-id');
            const username = this.getAttribute('data-client-username');
            const vehiclePlate = this.getAttribute('data-vehicle-plate');

            const modal = document.getElementById('editModal');
            modal.querySelector('#modal-vehicleId').value = vehicleId;
            modal.querySelector('#modal-username').value = username;
            modal.querySelector('#modal-licensePlate').value = vehiclePlate;
        });
    });

    // Handle form submission for update
    document.getElementById('updateVehicleForm').addEventListener('submit', function(event) {
        event.preventDefault(); // Prevent the default form submission

        const formData = new FormData(this);
        const vehicleId = formData.get('vehicleId'); // Get clientId from formData

        fetch(`/vehicle/${vehicleId}/edit`, {
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
            const vehicleId = this.getAttribute('data-vehicle-id');

            fetch(`/vehicle/${vehicleId}/delete`, {
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