// document.addEventListener('DOMContentLoaded', function() {
//     const changePasswordButton = document.getElementById('changePassword-button');
//     const passwordResetModal = new bootstrap.Modal(document.getElementById('passwordResetModal'));
//     const passwordResetForm = document.getElementById('passwordResetForm');
//
//     changePasswordButton.addEventListener('click', function() {
//         passwordResetModal.show();
//     });
//
//     passwordResetForm.addEventListener('submit', function(event) {
//         event.preventDefault(); // Prevent default form submission
//
//         const email = document.getElementById('email').value;
//
//         fetch('/forgot-password', {
//             method: 'POST',
//             headers: {
//                 'Content-Type': 'application/x-www-form-urlencoded',
//                 'X-CSRF-TOKEN': document.querySelector('input[name="${_csrf.parameterName}"]').value
//             },
//             body: new URLSearchParams({
//                 'email': email
//             })
//         })
//             .then(response => response.text())
//             .then(result => {
//                 // Handle the response
//                 alert(result); // Display the message to the user
//                 passwordResetModal.hide(); // Hide the modal after submission
//             })
//             .catch(error => {
//                 console.error('Error:', error);
//                 alert('An error occurred. Please try again.');
//             });
//     });
// });
