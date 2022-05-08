import request from '../utils/request';
// import axios from "axios";
export const fetchData = query => {
    return request({
        url: './table.json',
        method: 'get',
        params: query
    });
};

export const fetchBookData = query => {
    //query={'bookname':"书24"}
    return request({
        // url: 'http://127.0.0.1:5000/querybook',
        url: './book_test.json',
        method: 'get',
        params: query
    });
    // axios
    //     .post("http://127.0.0.1:5000" + "/querybook", query)
    //     .then((Response) => {
    //         return Response
    //       })
    // return;
};

export const fetchBorrowData = query => {
    return request({
        url: './book_borrow.json',
        method: 'get',
        params: query
    });
};


export const fetchBorrowListData = query => {
    return request({
        url: './borrowList.json',
        method: 'get',
        params: query
    });
};

export const sendSearchData = query => {
    return request({
        url: 'localhost:5000/search',
        method: 'post',
        params: query
    });
};


export const userData = query => {
    return request({
        url: './userList.json',
        method: 'get',
        params: query
    });
};


export const userOneData = query => {
    return request({
        url: 'localhost:5000/search_user',
        method: 'post',
        params: query
    });
};



export const commentData = query => {
    return request({
        url: './commentList.json',
        method: 'GET',
        params: query
    });
};


export const ruleData = query => {
    return request({
        url: './rule.json',
        method: 'GET',
        params: query
    });
};