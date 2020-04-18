const { promisify } = require('util')
const pool = require("./pool")

const pool_query = promisify(pool.query).bind(pool)
module.exports = {
    load: sql => pool_query(sql),
    add: (table, item) => {
        const key = Object.keys(item).filter(x => x != "id").join(',')
        const value = Object.keys(item).filter(x => x != "id").map(x => (
            `N'${typeof item[x] == typeof "a" ? item[x].split("'").join("''") : item[x]}'`
        )).join(',')
        return pool_query(`insert into ${table} (${key}) values (${value})`)
    },
    update: (table, item, id) => {
        const value = Object.keys(item).map(x => `${x} = N'${typeof item[x] == typeof "a" ? item[x].split("'").join("''") : item[x]}'`).join(',');
        return pool_query(`update ${table} set ${value} where id=${id}`)
    },
    getAllPaging: (table, condition, orderBy, page, rowPerPage) => {
        return pool_query(`SELECT *
        FROM ${table}
        ${condition ? `WHERE ${condition}` : ""} 
        ORDER BY ${orderBy || "Id"}
        LIMIT ${rowPerPage}
        OFFSET ${page * rowPerPage}`)
    },
    getNumberOfRows: (table) => pool_query(`select count(*) as Sum from ${table}`)
}