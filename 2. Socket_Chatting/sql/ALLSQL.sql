-- 채팅방 테이블
CREATE TABLE CHATTINGROOM (
    CHATTINGROOM_ID INT PRIMARY KEY,
    ROOMNAME VARCHAR2(100) NOT NULL,
    UNAME1 INT NOT NULL,
    UNAME2 INT NOT NULL,
    CONSTRAINT FK_ROOM_UNAME1 FOREIGN KEY (UNAME1) REFERENCES MEMBER(ID),
    CONSTRAINT FK_ROOM_UNAME2 FOREIGN KEY (UNAME2) REFERENCES MEMBER(ID)
);

CREATE SEQUENCE CHATTINGROOM_SEQ START WITH 1 INCREMENT BY 1 NOCACHE;

-- 채팅 리스트 (ON DELETE CASCADE)
CREATE TABLE CHATTINGLIST (
    CHATTINGLIST_ID INT PRIMARY KEY,
    CHATTINGROOM_ID INT NOT NULL,
    UNAME1 INT NOT NULL,
    UNAME2 INT NOT NULL,
    CONSTRAINT FK_CL_ROOM FOREIGN KEY (CHATTINGROOM_ID) REFERENCES CHATTINGROOM(CHATTINGROOM_ID) ON DELETE CASCADE
);

CREATE SEQUENCE CHATTINGLIST_SEQ START WITH 1 INCREMENT BY 1 NOCACHE;

-- 채팅 메시지 (ON DELETE CASCADE)
CREATE TABLE CHATMESSAGE (
    CHATTINGMESSAGE_ID INT PRIMARY KEY,
    CHATTINGROOM_ID INT NOT NULL,
    UNAME1 INT NOT NULL,
    CONTENT VARCHAR2(4000),
    REGDATE DATE DEFAULT SYSDATE,
    CONSTRAINT FK_CM_ROOM FOREIGN KEY (CHATTINGROOM_ID) REFERENCES CHATTINGROOM(CHATTINGROOM_ID) ON DELETE CASCADE
);

CREATE SEQUENCE CHATMESSAGE_ID START WITH 1 INCREMENT BY 1 NOCACHE;

--==================================================================================================--

-- 멀티 채팅방 테이블
CREATE TABLE MULTICHATTINGROOM (
    MULTICHATTINGROOM_ID INT PRIMARY KEY,
    MULTIROOMNAME VARCHAR2(100) NOT NULL,
    PARTICIPANT1_ID INT,
    PARTICIPANT2_ID INT,
    PARTICIPANT3_ID INT,
    PARTICIPANT4_ID INT,
    PARTICIPANT5_ID INT,
    CONSTRAINT FK_MCR_P1 FOREIGN KEY (PARTICIPANT1_ID) REFERENCES MEMBER(ID),
    CONSTRAINT FK_MCR_P2 FOREIGN KEY (PARTICIPANT2_ID) REFERENCES MEMBER(ID),
    CONSTRAINT FK_MCR_P3 FOREIGN KEY (PARTICIPANT3_ID) REFERENCES MEMBER(ID),
    CONSTRAINT FK_MCR_P4 FOREIGN KEY (PARTICIPANT4_ID) REFERENCES MEMBER(ID),
    CONSTRAINT FK_MCR_P5 FOREIGN KEY (PARTICIPANT5_ID) REFERENCES MEMBER(ID)
);

CREATE SEQUENCE MULTICHATTINGROOM_SEQ START WITH 1 INCREMENT BY 1 NOCACHE;

-- 멀티 채팅 리스트 (ON DELETE CASCADE)
CREATE TABLE MULTICHATTINGLIST (
    MULTICHATTINGLIST_ID INT PRIMARY KEY,
    MULTICHATTINGROOM_ID INT NOT NULL,
    ID INT NOT NULL,
    CONSTRAINT FK_MCL_ROOM FOREIGN KEY (MULTICHATTINGROOM_ID) REFERENCES MULTICHATTINGROOM(MULTICHATTINGROOM_ID) ON DELETE CASCADE,
    CONSTRAINT FK_MCL_USER FOREIGN KEY (ID) REFERENCES MEMBER(ID)
);

CREATE SEQUENCE MULTICHATTINGLIST_SEQ START WITH 1 INCREMENT BY 1 NOCACHE;

-- 멀티 채팅 메시지 (ON DELETE CASCADE)
CREATE TABLE MULTICHATMESSAGE (
    MULTICHATTINGMESSAGE_ID INT PRIMARY KEY,
    MULTICHATTINGROOM_ID INT NOT NULL,
    UNAME1 INT NOT NULL,
    CONTENT VARCHAR2(4000),
    REGDATE DATE DEFAULT SYSDATE,
    CONSTRAINT FK_MCM_ROOM FOREIGN KEY (MULTICHATTINGROOM_ID) REFERENCES MULTICHATTINGROOM(MULTICHATTINGROOM_ID) ON DELETE CASCADE
);

CREATE SEQUENCE MULTICHATMESSAGE_ID START WITH 1 INCREMENT BY 1 NOCACHE;
    