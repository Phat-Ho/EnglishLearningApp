const express = require('express');
const morgan = require('morgan');
var cors = require('cors');

const app = express();
app.use(morgan('dev'));
app.use(express.json());
app.use(cors());

const PORT = 5001

app.get('/', (req, res) => {

})

//Đường dẫn API
app.use('/api/quanlitudien', require('./controllers/DictionaryManagementController'))
app.use('/api/hash', require('./controllers/PasswordController'))


app.listen(PORT, () => {
    console.log(`API is running in Port ${PORT}`)
})
