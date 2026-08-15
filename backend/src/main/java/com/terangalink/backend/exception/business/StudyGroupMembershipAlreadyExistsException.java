package com.terangalink.backend.exception.business;

public class StudyGroupMembershipAlreadyExistsException extends RuntimeException {

    public StudyGroupMembershipAlreadyExistsException(String message) {
        super(message);
    }
}
