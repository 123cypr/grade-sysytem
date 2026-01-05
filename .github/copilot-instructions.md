# Copilot Instructions for Grade System

## Project Overview
This is a grade management system designed to handle student grades, course management, and academic performance tracking.

## Code Style and Best Practices

### General Guidelines
- Write clear, readable, and maintainable code
- Use meaningful variable and function names that describe their purpose
- Add comments for complex logic or non-obvious implementations
- Follow DRY (Don't Repeat Yourself) principles

### Testing
- Write unit tests for new features and bug fixes
- Ensure tests are clear and test one thing at a time
- Include both positive and negative test cases
- Mock external dependencies appropriately

### Security
- Validate all user inputs
- Sanitize data before storing or displaying
- Use parameterized queries to prevent SQL injection
- Implement proper authentication and authorization
- Never commit sensitive information (API keys, passwords, etc.)

## Project Structure
- Keep related code organized in appropriate directories
- Separate concerns (business logic, data access, presentation)
- Use consistent naming conventions across the project

## Documentation
- Update README.md when adding new features or changing setup procedures
- Document public APIs and interfaces
- Include usage examples where helpful
- Keep documentation in sync with code changes

## Git Practices
- Write clear, descriptive commit messages
- Keep commits focused on a single change or feature
- Reference issue numbers in commit messages when applicable

## Grade System Specific Guidelines
- Ensure grade calculations are accurate and handle edge cases
- Validate grade ranges and boundaries
- Consider academic policies and regulations in implementations
- Handle student data with privacy and security in mind
- Implement proper rounding and precision for numerical grades
